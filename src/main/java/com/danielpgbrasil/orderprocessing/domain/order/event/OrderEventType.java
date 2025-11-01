package com.danielpgbrasil.orderprocessing.domain.order.event;

import com.danielpgbrasil.orderprocessing.domain.order.OrderStatus;

public enum OrderEventType {
    CREATED,
    PICKING_STARTED,
    TRANSIT_STARTED,
    DELIVERED;

    public static OrderEventType fromStatus(OrderStatus status) {
        return switch (status) {
            case OrderStatus.CREATED -> CREATED;
            case OrderStatus.PICKING -> PICKING_STARTED;
            case OrderStatus.IN_TRANSIT -> TRANSIT_STARTED;
            case OrderStatus.DELIVERED -> DELIVERED;
        };
    }
}
