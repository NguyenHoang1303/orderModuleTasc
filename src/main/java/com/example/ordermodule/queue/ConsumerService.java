package com.example.ordermodule.queue;

import com.example.ordermodule.entity.Order;
import com.example.ordermodule.enums.InventoryStatus;
import com.example.ordermodule.enums.PaymentStatus;
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
            handlerOrder(orderExist);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Transactional
    public void handlerOrder(Order order) {
        Order store = orderService.findById(order.getId());
        try {
            if (order.getPaymentStatus().equals(PaymentStatus.PAID.name())
                    && order.getInventoryStatus().equals(InventoryStatus.OUT_OF_STOCK.name())) {
                order.setPaymentStatus(PaymentStatus.REFUND.name());
                rabbitTemplate.convertAndSend(DIRECT_EXCHANGE, DIRECT_ROUTING_KEY_PAY, new OrderEvent(store));
                return;
            }

            if (order.getPaymentStatus().equals(PaymentStatus.FAIL.name())
                    && order.getInventoryStatus().equals(InventoryStatus.DONE.name())) {
                order.setInventoryStatus(InventoryStatus.RETURN.name());
                rabbitTemplate.convertAndSend(DIRECT_EXCHANGE, DIRECT_ROUTING_KEY_INVENTORY, new OrderEvent(store));
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }


}
