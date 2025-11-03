package com.danielpgbrasil.orderprocessing.domain.order.event;

import com.danielpgbrasil.orderprocessing.domain.order.OrderStatus;

public enum OrderEventType {
    CREATED,
    PICKING_STARTED,
    TRANSIT_STARTED,
    DELIVERED;

    public static OrderEventType fromStatus(OrderStatus status) {
        return switch (status) {
            case CREATED -> CREATED;
            case PICKING -> PICKING_STARTED;
            case IN_TRANSIT -> TRANSIT_STARTED;
            case DELIVERED -> DELIVERED;
        };
    }
}
