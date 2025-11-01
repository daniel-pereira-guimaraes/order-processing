package com.danielpgbrasil.orderprocessing.application.order;

import com.danielpgbrasil.orderprocessing.domain.order.*;

public class GetOrderStatusUseCase {

    private final OrderRepository repository;

    public GetOrderStatusUseCase(OrderRepository repository) {
        this.repository = repository;
    }

    public OrderStatus getStatus(OrderId orderId) {
        var order = repository.getOrThrow(orderId);
        return order.status();
    }
}
