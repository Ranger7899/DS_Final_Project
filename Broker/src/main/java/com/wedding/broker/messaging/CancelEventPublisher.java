package com.wedding.broker.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CancelEventPublisher {

    private final RabbitTemplate rabbit;
    @Value("${broker.cancel.exchange}")   private String exchange;
    @Value("${broker.cancel.routingKey}") private String routingKey;

    public CancelEventPublisher(RabbitTemplate rabbit) { this.rabbit = rabbit; }

    public void publish(CancelReservationMessage msg) {
        rabbit.convertAndSend(exchange, routingKey, msg);
    }
}
