package com.wedding.broker.messaging;

import java.io.Serial;
import java.io.Serializable;

public class CancelReservationMessage implements Serializable {
    @Serial private static final long serialVersionUID = 1L;

    public enum SupplierType { VENUE, PHOTOGRAPHER, CATERING }

    private Long orderId;
    private SupplierType type;
    private String reservationId;
    private long timestamp;

    public CancelReservationMessage() {}

    public CancelReservationMessage(Long orderId, SupplierType type, String reservationId) {
        this.orderId = orderId;
        this.type = type;
        this.reservationId = reservationId;
        this.timestamp = System.currentTimeMillis();
    }

    public Long getOrderId()        { return orderId; }
    public SupplierType getType()   { return type; }
    public String getReservationId(){ return reservationId; }
    public long getTimestamp()      { return timestamp; }
}
