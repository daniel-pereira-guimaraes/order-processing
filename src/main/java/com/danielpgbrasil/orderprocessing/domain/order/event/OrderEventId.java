package com.danielpgbrasil.orderprocessing.domain.order.event;

import com.danielpgbrasil.orderprocessing.domain.shared.SurrogateId;

import java.util.Optional;

public class OrderEventId extends SurrogateId {

    protected OrderEventId(Long value) {
        super(value);
    }

    public static OrderEventId of(Long value) {
        return new OrderEventId(value);
    }

    public static Optional<OrderEventId> ofNullable(Long value) {
        return value == null ? Optional.empty()
                : Optional.of(new OrderEventId(value));
    }
}
