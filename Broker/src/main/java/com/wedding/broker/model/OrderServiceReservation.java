package com.wedding.broker.model;

import jakarta.persistence.*;

@Entity
@Table(name = "order_service_reservations")
public class OrderServiceReservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id") // Foreign key column in this table
    private Order order; // Reference back to the Order entity

    private String supplierId;
    private String serviceId;
    private String reservationId;
    private String serviceType; // e.g., "venue", "catering", "photographer" - added for map key

    // Default constructor required by JPA
    public OrderServiceReservation() {
    }

    public OrderServiceReservation(Order order, String serviceType, String supplierId, String serviceId, String reservationId) {
        this.order = order;
        this.serviceType = serviceType;
        this.supplierId = supplierId;
        this.serviceId = serviceId;
        this.reservationId = reservationId;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }
    public String getSupplierId() { return supplierId; }
    public void setSupplierId(String supplierId) { this.supplierId = supplierId; }
    public String getServiceId() { return serviceId; }
    public void setServiceId(String serviceId) { this.serviceId = serviceId; }
    public String getReservationId() { return reservationId; }
    public void setReservationId(String reservationId) { this.reservationId = reservationId; }
    public String getServiceType() { return serviceType; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }
}