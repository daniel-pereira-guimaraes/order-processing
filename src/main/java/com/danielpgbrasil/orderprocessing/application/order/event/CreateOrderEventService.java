package com.danielpgbrasil.orderprocessing.application.order.event;

import com.danielpgbrasil.orderprocessing.application.shared.AppTransaction;
import com.danielpgbrasil.orderprocessing.domain.order.Order;
import com.danielpgbrasil.orderprocessing.domain.order.event.*;

import com.danielpgbrasil.orderprocessing.domain.shared.AppClock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateOrderEventService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateOrderEventService.class);

    private final AppTransaction transaction;
    private final OrderEventRepository repository;
    private final AppClock clock;

    public CreateOrderEventService(AppTransaction transaction,
                                   OrderEventRepository repository,
                                   AppClock clock) {
        this.transaction = transaction;
        this.repository = repository;
        this.clock = clock;
    }

    public OrderEvent createEvent(Order order, OrderEventType type) {
        LOGGER.info("Criando evento: orderId={}, eventType={}", order.id().value(), type);
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
