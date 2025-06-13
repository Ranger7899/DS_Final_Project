package com.wedding.broker.messaging;

import com.rabbitmq.client.Channel;
import com.wedding.broker.client.CateringClient;
import com.wedding.broker.client.PhotographerClient;
import com.wedding.broker.client.VenueClient;
import com.wedding.broker.repository.OrderRepository;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
public class CancelProcessor {

    @Autowired private VenueClient venueClient;
    @Autowired private PhotographerClient photographerClient;
    @Autowired private CateringClient cateringClient;
    @Autowired private OrderRepository orderRepo;

    @RabbitListener(queues = "${broker.cancel.queue}", ackMode = "MANUAL")
    public void handle(CancelReservationMessage msg, Channel ch, Message raw) throws Exception {
        long tag = raw.getMessageProperties().getDeliveryTag();

        try {
            switch (msg.getType()) {
                case VENUE       -> venueClient.cancel(msg.getReservationId());
                case PHOTOGRAPHER-> photographerClient.cancel(msg.getReservationId());
                case CATERING    -> cateringClient.cancel(msg.getReservationId());
            }
            ch.basicAck(tag, false);
            // optional: mark supplier rolled back in DB here
        } catch (Exception ex) {
            boolean within15 =
                    Instant.ofEpochMilli(msg.getTimestamp())
                            .plus(Duration.ofMinutes(15))
                            .isAfter(Instant.now());

            if (within15) {
                ch.basicNack(tag, false, false);   // retry via DLX
            } else {
                ch.basicAck(tag, false);           // give up permanently
                // optional: mark rollback failure so ops can see it
            }
        }
    }
}
