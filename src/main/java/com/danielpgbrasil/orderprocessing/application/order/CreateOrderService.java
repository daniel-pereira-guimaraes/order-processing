package com.danielpgbrasil.orderprocessing.application.order;

import com.danielpgbrasil.orderprocessing.application.shared.AppTransaction;
import com.danielpgbrasil.orderprocessing.domain.order.*;

public class CreateOrderService {

    private final AppTransaction transaction;
    private final OrderRepository repository;
    private final OrderListener listener;

    public CreateOrderService(AppTransaction transaction,
                              OrderRepository repository,
                              OrderListener listener) {
        this.transaction = transaction;
        this.repository = repository;
        this.listener = listener;
    }

    public Order createOrder(OrderDetails details) {
        var order = buildOrder(details);
        transaction.execute(() -> repository.save(order));
        return order;
    }

    private Order buildOrder(OrderDetails details) {
        return Order.builder()
                .withDetails(details)
                .withStatus(OrderStatus.CREATED)
                .withListener(listener)
                .build();
    }
}
