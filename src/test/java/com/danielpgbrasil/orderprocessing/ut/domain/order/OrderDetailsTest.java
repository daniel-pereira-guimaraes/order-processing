package com.danielpgbrasil.orderprocessing.ut.domain.order;

import com.danielpgbrasil.orderprocessing.fixture.OrderItemFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static com.danielpgbrasil.orderprocessing.fixture.OrderDetailsFixture.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderDetailsTest {

    @Test
    void builderCreatesOrderDetailsWhenValid() {
        var orderDetails = builder().build();

        assertThat(orderDetails.customerName(), is(CUSTOMER_NAME));
        assertThat(orderDetails.customerAddress(), is(CUSTOMER_ADDRESS));
        assertThat(orderDetails.items(), hasSize(1));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    void builderThrowsExceptionWhenCustomerNameBlank(String name) {
        var builder = builder().withCustomerName(name);

        var exception = assertThrows(IllegalArgumentException.class, builder::build);

        assertThat(exception.getMessage(), is("O nome do cliente é requerido."));
    }

    @Test
    void builderThrowsExceptionWhenCustomerNameNull() {
        var builder = builder().withCustomerName(null);

        var exception = assertThrows(IllegalArgumentException.class, builder::build);

        assertThat(exception.getMessage(), is("O nome do cliente é requerido."));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    void builderThrowsExceptionWhenCustomerAddressBlank(String address) {
        var builder = builder().withCustomerAddress(address);

        var exception = assertThrows(IllegalArgumentException.class, builder::build);

        assertThat(exception.getMessage(), is("O endereço do cliente é requerido."));
    }

    @Test
    void builderThrowsExceptionWhenCustomerAddressNull() {
        var builder = builder().withCustomerAddress(null);

        var exception = assertThrows(IllegalArgumentException.class, builder::build);

        assertThat(exception.getMessage(), is("O endereço do cliente é requerido."));
    }

    @Test
    void builderThrowsExceptionWhenItemsEmpty() {
        var builder = builder().withItems(List.of());

        var exception = assertThrows(IllegalArgumentException.class, builder::build);

        assertThat(exception.getMessage(), is("Os itens do pedido são requeridos."));
    }

    @Test
    void builderThrowsExceptionWhenItemsNull() {
        var builder = builder().withItems(null);

        var exception = assertThrows(IllegalArgumentException.class, builder::build);

        assertThat(exception.getMessage(), is("Os itens do pedido são requeridos."));
    }

    @Test
    void itemsIsUnmodifiableWhenBuilt() {
        var items = builder().build().items();
        var newItem = OrderItemFixture.builder().build();

        var exception = assertThrows(UnsupportedOperationException.class,
                () -> items.add(newItem)
        );

        assertThat(exception, instanceOf(UnsupportedOperationException.class));
    }
}
