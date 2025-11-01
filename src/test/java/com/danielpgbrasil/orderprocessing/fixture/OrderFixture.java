package com.danielpgbrasil.orderprocessing.fixture;

import com.danielpgbrasil.orderprocessing.domain.order.Order;
import com.danielpgbrasil.orderprocessing.domain.order.OrderId;
import com.danielpgbrasil.orderprocessing.domain.order.OrderStatus;

public class OrderFixture {

    public static final OrderId ORDER_ID = OrderId.of(123L);

    private OrderFixture() {
    }

    public static Order.Builder builder() {
        return Order.builder()
                .withId(ORDER_ID)
                .withDetails(OrderDetailsFixture.builder().build())
                .withStatus(OrderStatus.CREATED);
    }
}
