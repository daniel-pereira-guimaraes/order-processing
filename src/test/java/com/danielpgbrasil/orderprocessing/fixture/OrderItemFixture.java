package com.danielpgbrasil.orderprocessing.fixture;

import com.danielpgbrasil.orderprocessing.domain.order.OrderItem;

import java.math.BigDecimal;

public class OrderItemFixture {

    private static final long PRODUCT_ID = 1L;
    private static final int QUANTITY = 5;
    private static final BigDecimal PRICE = BigDecimal.TEN;

    private OrderItemFixture() {
    }

    public static OrderItem.Builder builder() {
        return OrderItem.builder()
                .withProductId(PRODUCT_ID)
                .withQuantity(QUANTITY)
                .withPrice(PRICE);
    }
}
