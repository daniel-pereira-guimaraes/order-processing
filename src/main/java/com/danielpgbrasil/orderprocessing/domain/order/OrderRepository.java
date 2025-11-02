package com.danielpgbrasil.orderprocessing.domain.order;

import java.util.Optional;

public interface OrderRepository {
    void save(Order order);
    Optional<Order> get(OrderId id);
    Order getOrThrow(OrderId id);
    boolean exists(OrderId id);
}
