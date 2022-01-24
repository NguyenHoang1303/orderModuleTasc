package com.example.ordermodule.queue;


import com.example.ordermodule.dto.OrderDto;
import com.example.ordermodule.fcm.FCMService;
import com.example.ordermodule.service.OrderService;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.example.ordermodule.queue.Config.*;

@Component
@Log4j2
public class ReceiveMessage {

    @Autowired
    OrderService orderService;

    @Autowired
    ConsumerService consumerService;

    @Autowired
    private FCMService fcmService;

    @RabbitListener(queues = {QUEUE_ORDER_PAY})
    public void getInfoPayment(OrderDto orderDto) {
        consumerService.handlerPayment(orderDto);
        log.info("order nhận payment " + orderDto);
//        PnsRequest pnsRequest = new PnsRequest();
//        pnsRequest.setFcmToken(paymentDto.getDevice_token());
//        pnsRequest.setContent(paymentDto.getMessage());
//        pnsRequest.setTitle("Order " + paymentDto.getOrderId());
//        fcmService.pushNotification(pnsRequest);
    }

    @RabbitListener(queues = {QUEUE_ORDER_INVENTORY})
    public void getInfoInventory(OrderDto orderDto) {
        consumerService.handlerInventory(orderDto);



    }
}
