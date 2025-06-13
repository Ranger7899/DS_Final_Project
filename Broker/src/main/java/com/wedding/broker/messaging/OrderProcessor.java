package com.wedding.broker.messaging;

import com.rabbitmq.client.Channel;
import com.wedding.broker.client.CateringClient;
import com.wedding.broker.client.PhotographerClient;
import com.wedding.broker.client.VenueClient;
import com.wedding.broker.model.Order;
import com.wedding.broker.repository.OrderRepository;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
public class OrderProcessor {

    @Autowired private CancelEventPublisher cancelPublisher;

    @Autowired private OrderRepository orderRepo;
    @Autowired private VenueClient venueClient;
    @Autowired private PhotographerClient photographerClient;
    @Autowired private CateringClient cateringClient;

    // Ack mode MANUAL lets us decide whether to retry
    @RabbitListener(queues = "${broker.order.queue}", ackMode = "MANUAL")
    public void onMessage(OrderCreatedMessage msg, Channel channel, Message raw) throws Exception {
        long tag = raw.getMessageProperties().getDeliveryTag();

        Order order = orderRepo.findById(msg.getOrderId())
                .orElseThrow(() -> new IllegalStateException("Order not found: " + msg.getOrderId()));
        try {
            if (order.getVenueId()        != null) venueClient.confirm(order.getVenueId());
            if (order.getPhotographerId() != null) photographerClient.confirm(order.getPhotographerId());
            if (order.getCateringId()     != null) cateringClient.confirm(order.getCateringId());

            order.setStatus("CONFIRMED");
            order.setUpdatedAt(java.time.LocalDateTime.now());
            orderRepo.save(order);

            channel.basicAck(tag, false);               // success ✅

        } catch (Exception ex) {
            // Still inside 15-min window? → dead-letter for retry
            boolean within15 =
                    Instant.ofEpochMilli(msg.getTimestamp())
                            .plus(Duration.ofMinutes(15))
                            .isAfter(Instant.now());

            if (within15) {
                channel.basicNack(tag, false, false);   // send to retry queue
            } else {
                order.setStatus("ROLLBACK_PENDING");
                orderRepo.save(order);

                if (order.getVenueId() != null) {
                    cancelPublisher.publish(new CancelReservationMessage(
                            order.getId(),
                            CancelReservationMessage.SupplierType.VENUE,
                            order.getVenueId()));
                }
                if (order.getPhotographerId() != null) {
                    cancelPublisher.publish(new CancelReservationMessage(
                            order.getId(),
                            CancelReservationMessage.SupplierType.PHOTOGRAPHER,
                            order.getPhotographerId()));
                }
                if (order.getCateringId() != null) {
                    cancelPublisher.publish(new CancelReservationMessage(
                            order.getId(),
                            CancelReservationMessage.SupplierType.CATERING,
                            order.getCateringId()));
                }

                channel.basicAck(tag, false);   // we did our job – no more confirm retries
                return;

            }
        }
    }
}
