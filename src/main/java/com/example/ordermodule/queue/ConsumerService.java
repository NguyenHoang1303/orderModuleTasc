package com.example.ordermodule.queue;

import com.example.ordermodule.dto.OrderDto;
import com.example.ordermodule.entity.Order;
import com.example.ordermodule.enums.InventoryStatus;
import com.example.ordermodule.enums.PaymentStatus;
import com.example.ordermodule.service.OrderService;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.ordermodule.queue.Config.*;

@Service
@Log4j2
public class ConsumerService {

    @Autowired
    OrderService orderService;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Transactional
    public void handlerInventory(OrderDto orderDto) {
        try {
            if (!orderDto.validationInventory()) return;
            Order orderExist = orderService.findById(orderDto.getOrderId());
            if (orderExist == null) {
                return;
            }
            orderExist.setInventoryStatus(orderDto.getInventoryStatus());
            log.info("1.xử lý kho: " + orderDto);
            handlerOrder(orderExist);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Transactional
    public void handlerPayment(OrderDto orderDto) {
        try {
            if (!orderDto.validationPayment()) return;
            Order orderExist = orderService.findById(orderDto.getOrderId());
            if (orderExist == null) {
                log.error("hoá đơn không tồn tại");
                return;
            }

            orderExist.setPaymentStatus(orderDto.getPaymentStatus());
            handlerOrder(orderExist);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Transactional
    public void handlerOrder(Order order) {
        log.error("1.handlerOrder");
        if (order.getPaymentStatus().equals(PaymentStatus.PAID.name())
                && order.getInventoryStatus().equals(InventoryStatus.OUT_OF_STOCK.name())) {
            log.error("2.Payment Fail");
            order.setPaymentStatus(PaymentStatus.REFUND.name());
            rabbitTemplate.convertAndSend(DIRECT_EXCHANGE, DIRECT_ROUTING_KEY_PAY, new OrderDto(orderService.save(order)));
            return;
        }

        if (order.getPaymentStatus().equals(PaymentStatus.FAIL.name())
                && order.getInventoryStatus().equals(InventoryStatus.DONE.name())) {
            log.error("3.handlerOrder");
            order.setInventoryStatus(InventoryStatus.RETURN.name());
            rabbitTemplate.convertAndSend(DIRECT_EXCHANGE, DIRECT_ROUTING_KEY_INVENTORY, new OrderDto(orderService.save(order)));
        }
        log.info("4. end handlerOrder");
    }


}
