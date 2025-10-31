package com.danielpgbrasil.orderprocessing.domain.order;

import com.danielpgbrasil.orderprocessing.domain.shared.SurrogateId;

import java.util.Optional;

public class OrderId extends SurrogateId {

    protected OrderId(Long value) {
        super(value);
    }

    public static OrderId of(Long value) {
        return new OrderId(value);
    }

    public static Optional<OrderId> ofNullable(Long value) {
        return value == null ? Optional.empty()
                : Optional.of(new OrderId(value));
    }
}
