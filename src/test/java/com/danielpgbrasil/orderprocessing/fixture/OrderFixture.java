package com.danielpgbrasil.orderprocessing.fixture;

import com.danielpgbrasil.orderprocessing.domain.order.Order;
import com.danielpgbrasil.orderprocessing.domain.order.OrderId;
import com.danielpgbrasil.orderprocessing.domain.order.OrderListener;
import com.danielpgbrasil.orderprocessing.domain.order.OrderStatus;

import static org.mockito.Mockito.mock;

public class OrderFixture {

    public static final OrderId ORDER_ID = OrderId.of(123L);

    private OrderFixture() {
    }

    public static Order.Builder builder() {
        return Order.builder()
                .withId(ORDER_ID)
                .withDetails(OrderDetailsFixture.builder().build())
                .withStatus(OrderStatus.CREATED)
                .withListener(mock(OrderListener.class));
    }
}
