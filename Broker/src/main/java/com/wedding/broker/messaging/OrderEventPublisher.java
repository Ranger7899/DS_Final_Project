package com.wedding.broker.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class OrderEventPublisher {

    private final RabbitTemplate rabbit;
    @Value("${broker.order.exchange}")   private String exchange;
    @Value("${broker.order.routingKey}") private String routingKey;

    public OrderEventPublisher(RabbitTemplate rabbit) {
        this.rabbit = rabbit;
    }

    public void publish(Long orderId) {
        rabbit.convertAndSend(exchange, routingKey, new OrderCreatedMessage(orderId));
    }
}
