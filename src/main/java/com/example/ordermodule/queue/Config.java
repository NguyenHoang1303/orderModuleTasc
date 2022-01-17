package com.example.ordermodule.queue;


import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.springframework.amqp.core.BindingBuilder.bind;

@Configuration
public class Config {

    public static final String QUEUE_ORDER = "direct.queue.order";
    public static final String QUEUE_PAY = "direct.queue.pay";
    public static final String DIRECT_EXCHANGE = "direct.exchange";
    public static final String DIRECT_ROUTING_KEY_ORDER = "direct.routingKeyOrder";
    public static final String DIRECT_ROUTING_KEY_PAY = "direct.routingKeyPay";

    public static final String TOPIC_QUEUE_ORDER = "topic.queue.order";
    public static final String TOPIC_EXCHANGE = "topic.exchange";
    public static final String TOPIC_ROUTING_KEY = "topic.routingKeyOrder";

    @Bean
    public Declarables directBinding() {
        Queue directQueue = new Queue(QUEUE_ORDER);
        Queue directQueuePay = new Queue(QUEUE_PAY);
        DirectExchange directExchange = new DirectExchange(DIRECT_EXCHANGE);
        return new Declarables(
                directQueue,
                directExchange,
                bind(directQueue).to(directExchange).with(DIRECT_ROUTING_KEY_ORDER),
                bind(directQueuePay).to(directExchange).with(DIRECT_ROUTING_KEY_PAY)
        );
    }

    public Declarables topicBinding() {
        Queue topicQueueOrder = new Queue(TOPIC_QUEUE_ORDER);

        TopicExchange topicExchange = new TopicExchange(TOPIC_EXCHANGE);
        return  new Declarables();
    }


    @Bean
    public MessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate template(ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(converter());
        return rabbitTemplate;
    }

}
