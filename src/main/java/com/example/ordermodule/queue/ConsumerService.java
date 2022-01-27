package com.example.ordermodule.queue;

import com.example.ordermodule.entity.Order;
import com.example.ordermodule.enums.InventoryStatus;
import com.example.ordermodule.enums.PaymentStatus;
import com.example.ordermodule.fcm.FCMService;
import com.example.ordermodule.fcm.PnsRequest;
import com.example.ordermodule.service.OrderService;
import com.example.ordermodule.translate.TranslationService;
import common.event.OrderEvent;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.ordermodule.constant.KeyI18n.ORDER_NOTFOUND;
import static com.example.ordermodule.queue.Config.*;

@Service
@Log4j2
public class ConsumerService {

    @Autowired
    private FCMService fcmService;

    @Autowired
    OrderService orderService;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    TranslationService translationService;

    @Transactional
    public void handlerMessage(OrderEvent orderEvent) {
        try {
            if (!orderEvent.validationPayment()) return;
            Order orderExist = orderService.findById(orderEvent.getOrderId());
            if (orderExist == null) {
                log.error(translationService.translate(ORDER_NOTFOUND));
                return;
            }
            if (orderEvent.getQueueName().equals(QUEUE_PAY)) {
                orderExist.setPaymentStatus(orderEvent.getPaymentStatus());
            }
            if (orderEvent.getQueueName().equals(QUEUE_INVENTORY)) {
                orderExist.setInventoryStatus(orderEvent.getInventoryStatus());
            }
            handlerOrder(orderExist.getId());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Transactional
    public void handlerOrder(Long orderId) {
        Order order = orderService.findById(orderId);
        try {
            if (order.getPaymentStatus().equals(PaymentStatus.PAID.name())
                    && order.getInventoryStatus().equals(InventoryStatus.OUT_OF_STOCK.name())) {
                order.setPaymentStatus(PaymentStatus.REFUND.name());
                rabbitTemplate.convertAndSend(DIRECT_EXCHANGE, DIRECT_ROUTING_KEY_PAY, new OrderEvent(order));
                return;
            }

            if (order.getPaymentStatus().equals(PaymentStatus.FAIL.name())
                    && order.getInventoryStatus().equals(InventoryStatus.DONE.name())) {
                order.setInventoryStatus(InventoryStatus.RETURN.name());
                rabbitTemplate.convertAndSend(DIRECT_EXCHANGE, DIRECT_ROUTING_KEY_INVENTORY, new OrderEvent(order));
            }
            pushNotification(orderId);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void pushNotification(Long orderId) {
        log.warn("orderId: " + orderId);
        PnsRequest pnsRequest = new PnsRequest();
        Order order = orderService.findById(orderId);
        pnsRequest.setFcmToken("dyVv77AFu561i3UONuCqSV:APA91bGXkO2VjRvDmQm2wh45K4WTn18pJn2l6DXWMkUC8FTLHFgBVtbfBBzcYBzq_kXVkoTL8xm_mp3PPLG0hJUxDTIw_x6ZxGx7ShWHnaozyW8JpGQN-KHvip5Cb0P5qYiFj_Ap83rt");
        pnsRequest.setTitle("Order " + order.getId());
        boolean checkFailOrder = (order.getPaymentStatus().equals(PaymentStatus.FAIL.name()) || order.getPaymentStatus().equals(PaymentStatus.REFUNDED.name()))
                && (order.getInventoryStatus().equals(InventoryStatus.OUT_OF_STOCK.name()) || order.getInventoryStatus().equals(InventoryStatus.RETURNED.name()));

        if (order.getPaymentStatus().equals(PaymentStatus.PAID.name())
                && order.getInventoryStatus().equals(InventoryStatus.DONE.name())) {
            pnsRequest.setContent("Order successfully!");
            fcmService.pushNotification(pnsRequest);
        } else if (checkFailOrder) {
            pnsRequest.setContent("Order fail!");
            fcmService.pushNotification(pnsRequest);
        }
    }


}
