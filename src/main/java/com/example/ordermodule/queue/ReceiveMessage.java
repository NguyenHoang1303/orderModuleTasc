package com.example.ordermodule.queue;


import com.example.ordermodule.fcm.FCMService;
import com.example.ordermodule.fcm.PnsRequest;
import com.example.ordermodule.service.OrderService;
import common.event.OrderEvent;
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

    @RabbitListener(queues = {QUEUE_ORDER})
    public void getMessage(OrderEvent orderEvent) {
        consumerService.handlerMessage(orderEvent);
        PnsRequest pnsRequest = new PnsRequest();
        pnsRequest.setFcmToken("dyVv77AFu561i3UONuCqSV:APA91bGXkO2VjRvDmQm2wh45K4WTn18pJn2l6DXWMkUC8FTLHFgBVtbfBBzcYBzq_kXVkoTL8xm_mp3PPLG0hJUxDTIw_x6ZxGx7ShWHnaozyW8JpGQN-KHvip5Cb0P5qYiFj_Ap83rt");
        pnsRequest.setContent(orderEvent.getMessage() == null ? "fail" : orderEvent.getMessage());
        pnsRequest.setTitle("Order " + orderEvent.getOrderId());
        System.out.println(pnsRequest.toString());
        fcmService.pushNotification(pnsRequest);
    }
}
