package com.danielpgbrasil.orderprocessing.fixture;

import com.danielpgbrasil.orderprocessing.domain.order.OrderDetails;
import com.danielpgbrasil.orderprocessing.domain.order.OrderItem;

import java.util.List;

public class OrderDetailsFixture {

    public static final String CUSTOMER_NAME = "João Silva";
    public static final String CUSTOMER_ADDRESS = "Rua Exemplo, 123, São Paulo/SP";
    public static final List<OrderItem> ITEMS = List.of(OrderItemFixture.builder().build());

    private OrderDetailsFixture() {
    }

    public static OrderDetails.Builder builder() {
        return OrderDetails.builder()
                .withCustomerName(CUSTOMER_NAME)
                .withCustomerAddress(CUSTOMER_ADDRESS)
                .withItems(ITEMS);
    }
}
