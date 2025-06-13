package com.wedding.broker.messaging;

import java.io.Serial;
import java.io.Serializable;

public class OrderCreatedMessage implements Serializable {
    @Serial private static final long serialVersionUID = 1L;

    private Long orderId;
    private long timestamp;          // epoch millis

    public OrderCreatedMessage() { }

    public OrderCreatedMessage(Long orderId) {
        this.orderId = orderId;
        this.timestamp = System.currentTimeMillis();
    }

    public Long getOrderId()   { return orderId; }
    public long getTimestamp() { return timestamp; }
}
