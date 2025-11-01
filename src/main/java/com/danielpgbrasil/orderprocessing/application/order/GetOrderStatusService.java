package com.danielpgbrasil.orderprocessing.application.order;

import com.danielpgbrasil.orderprocessing.domain.order.*;

public class GetOrderStatusService {

    private final OrderRepository repository;

    public GetOrderStatusService(OrderRepository repository) {
        this.repository = repository;
    }

    public OrderStatus getStatus(OrderId orderId) {
        var order = repository.getOrThrow(orderId);
        return order.status();
    }
}
