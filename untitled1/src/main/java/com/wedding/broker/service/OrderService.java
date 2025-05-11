package com.wedding.broker.service;

import com.wedding.broker.client.VenueClient;
import com.wedding.broker.model.Order;
import com.wedding.broker.model.OrderRequest;
import com.wedding.broker.model.Reservation;
import com.wedding.broker.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

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
        Reservation venueReservation = venueClient.reserve(
                orderRequest.getVenueId(), orderRequest.getDate(), orderRequest.getLocation(), 300);

        // Check if the reservation succeeded
        if (venueReservation != null) {
            // Confirm the reservation
            venueClient.confirm(venueReservation.getId());

            // Save order
            Order order = new Order();
            order.setUserId(orderRequest.getUserId());
            order.setDate(orderRequest.getDate());
            order.setLocation(orderRequest.getLocation());

            // Populate services map with venue reservation
            Map<String, Order.ServiceReservation> services = new HashMap<>();
//            services.put("venue", new Order.ServiceReservation(
//                    venueReservation.getSupplierId(), venueReservation.getServiceId(), venueReservation.getId()));
            order.setServices(services);

            order.setStatus("confirmed");
            order.setCreatedAt(LocalDateTime.now());
            order.setUpdatedAt(LocalDateTime.now());
            orderRepository.save(order);
            return order;
        } else {
            // Cancel the reservation if it was made
            if (venueReservation != null) {
                venueClient.cancel(venueReservation.getId());
            }
            throw new RuntimeException("Failed to reserve venue service");
        }
    }
}