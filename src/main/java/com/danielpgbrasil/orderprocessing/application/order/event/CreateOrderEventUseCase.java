package com.danielpgbrasil.orderprocessing.application.order.event;

import com.danielpgbrasil.orderprocessing.application.shared.AppTransaction;
import com.danielpgbrasil.orderprocessing.domain.order.Order;
import com.danielpgbrasil.orderprocessing.domain.order.event.*;

import com.danielpgbrasil.orderprocessing.domain.shared.AppClock;

public class CreateOrderEventUseCase {

    private final AppTransaction transaction;
    private final OrderEventRepository repository;
    private final AppClock clock;

    public CreateOrderEventUseCase(AppTransaction transaction,
                                   OrderEventRepository repository,
                                   AppClock clock) {
        this.transaction = transaction;
        this.repository = repository;
        this.clock = clock;
    }

    public OrderEvent createEvent(Order order, OrderEventType type) {
        var event = buildEvent(order, type);
        transaction.execute(() -> repository.save(event));
        return event;
    }

    private OrderEvent buildEvent(Order order, OrderEventType type) {
        return OrderEvent.builder()
                .withOrderId(order.id())
                .withType(type)
                .withCreatedAt(clock.now())
                .withPublished(false)
                .build();
    }
}
