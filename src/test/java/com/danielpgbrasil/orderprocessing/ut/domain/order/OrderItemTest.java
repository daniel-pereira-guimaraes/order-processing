package com.danielpgbrasil.orderprocessing.ut.domain.order;

import com.danielpgbrasil.orderprocessing.domain.order.OrderItem;
import com.danielpgbrasil.orderprocessing.fixture.OrderItemFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
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

    static Stream<Arguments> sameReferenceProvider() {
        var orderItem = OrderItemFixture.builder().build();
        return Stream.of(Arguments.of(orderItem, orderItem));
    }

    @ParameterizedTest
    @MethodSource("sameReferenceProvider")
    void equalsReturnsTrueForSameReference(OrderItem a, OrderItem b) {
        assertThat(a.equals(b), is(true));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "dummy"})
    void equalsReturnsFalseForNullOrDifferentClass(Object other) {
        var orderItem = OrderItemFixture.builder().build();

        assertThat(orderItem.equals(other), is(false));
    }
    @Test
    void equalsReturnsTrueForEqualOrderItems() {
        var orderItem1 = OrderItemFixture.builder().build();
        var orderItem2 = OrderItemFixture.builder().build();
        assertThat(orderItem1.equals(orderItem2), is(true));
    }

    @Test
    void equalsReturnsFalseForDifferentProductId() {
        var orderItem1 = OrderItemFixture.builder().withProductId(1L).build();
        var orderItem2 = OrderItemFixture.builder().withProductId(2L).build();
        assertThat(orderItem1.equals(orderItem2), is(false));
    }

    @Test
    void equalsReturnsFalseForDifferentQuantity() {
        var orderItem1 = OrderItemFixture.builder().withQuantity(5).build();
        var orderItem2 = OrderItemFixture.builder().withQuantity(10).build();
        assertThat(orderItem1.equals(orderItem2), is(false));
    }

    @Test
    void equalsReturnsFalseForDifferentPrice() {
        var orderItem1 = OrderItemFixture.builder().withPrice(BigDecimal.TEN).build();
        var orderItem2 = OrderItemFixture.builder().withPrice(BigDecimal.ONE).build();
        assertThat(orderItem1.equals(orderItem2), is(false));
    }

    @Test
    void hashCodeIsEqualForEqualOrderItems() {
        var orderItem1 = OrderItemFixture.builder().build();
        var orderItem2 = OrderItemFixture.builder().build();
        assertThat(orderItem1.hashCode(), is(orderItem2.hashCode()));
    }

    @Test
    void hashCodeIsDifferentForDifferentOrderItems() {
        var orderItem1 = OrderItemFixture.builder().withProductId(1L).build();
        var orderItem2 = OrderItemFixture.builder().withProductId(2L).build();
        assertThat(orderItem1.hashCode(), not(orderItem2.hashCode()));
    }
}
