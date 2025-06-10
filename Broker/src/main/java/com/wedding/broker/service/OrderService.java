// src/main/java/com/wedding/broker/service/OrderService.java
package com.wedding.broker.service;

import com.wedding.broker.client.VenueClient;
import com.wedding.broker.model.Order;
import com.wedding.broker.model.OrderRequest;
import com.wedding.broker.model.VenueReservation; // Ensure this imports the updated broker Reservation model
import com.wedding.broker.model.OrderServiceReservation;
import com.wedding.broker.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class OrderService {
    @Autowired
    private VenueClient venueClient;
    @Autowired
    private OrderRepository orderRepository;

    public Order placeOrder(OrderRequest orderRequest) {
        // Validate user information
        if (orderRequest.getUserId() == null || orderRequest.getDate() == null || orderRequest.getLocation() == null) {
            throw new IllegalArgumentException("Invalid order details");
        }

        // Reserve the venue service with a 5-minute timeout
        // The timeout parameter will be sent to the venue service
        VenueReservation venueReservation = venueClient.reserve(
                orderRequest.getVenueId(), orderRequest.getDate(), orderRequest.getLocation(), 300);

        // Check if the reservation succeeded (venueReservation object is not null and has a valid ID)
        if (venueReservation != null && venueReservation.getId() != null) {
            // Confirm the reservation
            venueClient.confirm(venueReservation.getId());

            // Create and save the order
            Order order = new Order();
            order.setUserId(orderRequest.getUserId());
            order.setDate(orderRequest.getDate());
            order.setLocation(orderRequest.getLocation());

            // Populate services list with venue reservation
            // Use venueReservation.getVenueId() for supplierId, and venueReservation.getId() for reservationId
            OrderServiceReservation venueOrderService = new OrderServiceReservation(
                    order, "venue", venueReservation.getVenueId(), venueReservation.getVenueId(), venueReservation.getId());
            order.getServices().add(venueOrderService);

            order.setStatus("confirmed");
            order.setCreatedAt(LocalDateTime.now());
            order.setUpdatedAt(LocalDateTime.now());
            orderRepository.save(order);
            return order;
        } else {
            // Cancel the reservation if it was made (optional, as reserve would typically throw an exception on failure)
            if (venueReservation != null && venueReservation.getId() != null) {
                venueClient.cancel(venueReservation.getId());
            }
            throw new RuntimeException("Failed to reserve venue service");
        }
    }
}