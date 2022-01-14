package com.example.ordermodule.queue;


import com.example.ordermodule.controller.OrderController;
import com.example.ordermodule.dto.PaymentDto;
import com.example.ordermodule.entity.Order;
import com.example.ordermodule.fcm.FCMService;
import com.example.ordermodule.fcm.PnsRequest;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.example.ordermodule.queue.Config.QUEUE_PAY;

@Component
public class ReceiveMessage {

    @Autowired
    OrderController orderController;

    @Autowired
    private FCMService fcmService;

    @RabbitListener(queues = {QUEUE_PAY})
    public void getInfoPayment(PaymentDto paymentDto) {
        Order order =orderController.handlerOrder(paymentDto);
        System.out.println("Module Order nhận thông tin thanh toán thành công: "
                + order);
        PnsRequest pnsRequest = new PnsRequest();
        pnsRequest.setFcmToken(paymentDto.getDevice_token());
        pnsRequest.setContent(paymentDto.getMessage());
        pnsRequest.setTitle("Order " + paymentDto.getOrderId());
        fcmService.pushNotification(pnsRequest);
    }

}
