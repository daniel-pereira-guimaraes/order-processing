package com.danielpgbrasil.orderprocessing.ut.domain.order;

import com.danielpgbrasil.orderprocessing.fixture.OrderItemFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderItemTest {

    @Test
    void builderCreatesOrderItemWhenValid() {
        var orderItem = OrderItemFixture.builder().build();

        assertThat(orderItem.productId(), is(1L));
        assertThat(orderItem.quantity(), is(5));
        assertThat(orderItem.price(), is(BigDecimal.TEN));
    }

    @Test
    void builderThrowsExceptionWhenProductIdIsNull() {
        var builder = OrderItemFixture.builder().withProductId(null);
        var exception = assertThrows(IllegalArgumentException.class, builder::build);
        assertThat(exception.getMessage(), is("O id do produto é requerido."));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, 1000})
    void builderThrowsExceptionWhenQuantityOutOfRange(int quantity) {
        var builder = OrderItemFixture.builder().withQuantity(quantity);
        var exception = assertThrows(IllegalArgumentException.class, builder::build);
        assertThat(exception.getMessage(), is("A quantidade deve ser de 1 até 999."));
    }

    @Test
    void builderThrowsExceptionWhenQuantityIsNull() {
        var builder = OrderItemFixture.builder().withQuantity(null);
        var exception = assertThrows(IllegalArgumentException.class, builder::build);
        assertThat(exception.getMessage(), is("A quantidade é requerida."));
    }

    @ParameterizedTest
    @ValueSource(doubles = {-0.01, 1000000})
    void builderThrowsExceptionWhenPriceOutOfRange(double price) {
        var builder = OrderItemFixture.builder().withPrice(BigDecimal.valueOf(price));
        var exception = assertThrows(IllegalArgumentException.class, builder::build);
        assertThat(exception.getMessage(), is("O preço deve ser de 0 até 999999.99"));
    }

    @Test
    void builderThrowsExceptionWhenPriceIsNull() {
        var builder = OrderItemFixture.builder().withPrice(null);
        var exception = assertThrows(IllegalArgumentException.class, builder::build);
        assertThat(exception.getMessage(), is("O preço é requerido."));
    }
}
