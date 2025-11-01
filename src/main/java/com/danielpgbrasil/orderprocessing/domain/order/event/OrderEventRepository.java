package com.danielpgbrasil.orderprocessing.domain.order.event;

import com.danielpgbrasil.orderprocessing.domain.order.OrderId;

import java.util.List;
import java.util.Optional;

public interface OrderEventRepository {
    void save(OrderEvent orderEvent);
    Optional<OrderEvent> get(OrderEventId id);
    OrderEvent getOrThrow(OrderEventId id);
    List<OrderEvent> findAllUnpublished();
    List<OrderEvent> findByOrderId(OrderId orderId);
}
