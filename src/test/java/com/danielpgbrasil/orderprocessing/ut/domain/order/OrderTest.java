package com.danielpgbrasil.orderprocessing.ut.domain.order;

import com.danielpgbrasil.orderprocessing.domain.order.Order;
import com.danielpgbrasil.orderprocessing.domain.order.OrderId;
import com.danielpgbrasil.orderprocessing.domain.order.OrderStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static com.danielpgbrasil.orderprocessing.fixture.OrderFixture.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderTest {

    @Test
    void builderCreatesOrderWhenValid() {
        var order = builder().build();

        assertThat(order.id(), is(ORDER_ID));
        assertThat(order.details(), is(notNullValue()));
        assertThat(order.status(), is(OrderStatus.CREATED));
    }

    @Test
    void builderThrowsExceptionWhenDetailsNull() {
        var builder = builder().withDetails(null);

        var exception = assertThrows(IllegalArgumentException.class, builder::build);

        assertThat(exception.getMessage(), is("Os detalhes do pedido são requeridos."));
    }

    @Test
    void builderThrowsExceptionWhenStatusNull() {
        var builder = builder().withStatus(null);

        var exception = assertThrows(IllegalArgumentException.class, builder::build);

        assertThat(exception.getMessage(), is("O status do pedido é requerido."));
    }

    @Test
    void finalizeCreationSetsIdAndStatus() {
        var order = builder().withId(null).withStatus(OrderStatus.PICKING).build();

        order.finalizeCreation(ORDER_ID);

        assertThat(order.id(), is(ORDER_ID));
        assertThat(order.status(), is(OrderStatus.CREATED));
    }

    @Test
    void finalizeCreationThrowsWhenAlreadyFinalized() {
        var order = builder().build();

        var exception = assertThrows(IllegalStateException.class,
                () -> order.finalizeCreation(ORDER_ID)
        );

        assertThat(exception.getMessage(), is("A criação do pedido já foi finalizada."));
    }

    @Test
    void startPickingChangesStatus() {
        var order = builder().build();

        order.startPicking();

        assertThat(order.status(), is(OrderStatus.PICKING));
    }

    @Test
    void startPickingThrowsIfStatusNotCreated() {
        var order = builder().withStatus(OrderStatus.PICKING).build();

        var exception = assertThrows(IllegalStateException.class, order::startPicking);

        assertThat(exception.getMessage(), is("Status inválido: esperado CREATED, mas está PICKING."));
    }

    @Test
    void startTransitChangesStatus() {
        var order = builder().withStatus(OrderStatus.PICKING).build();

        order.startTransit();

        assertThat(order.status(), is(OrderStatus.IN_TRANSIT));
    }

    @Test
    void startTransitThrowsIfStatusNotPicking() {
        var order = builder().withStatus(OrderStatus.CREATED).build();

        var exception = assertThrows(IllegalStateException.class, order::startTransit);

        assertThat(exception.getMessage(), is("Status inválido: esperado PICKING, mas está CREATED."));
    }

    @Test
    void markDeliveredChangesStatus() {
        var order = builder().withStatus(OrderStatus.IN_TRANSIT).build();

        order.markDelivered();

        assertThat(order.status(), is(OrderStatus.DELIVERED));
    }

    @Test
    void markDeliveredThrowsIfStatusNotInTransit() {
        var order = builder().withStatus(OrderStatus.PICKING).build();

        var exception = assertThrows(IllegalStateException.class, order::markDelivered);

        assertThat(exception.getMessage(), is("Status inválido: esperado IN_TRANSIT, mas está PICKING."));
    }

    static Stream<Arguments> sameReferenceProvider() {
        var order = builder().build();
        return Stream.of(Arguments.of(order, order));
    }

    @ParameterizedTest
    @MethodSource("sameReferenceProvider")
    void equalsReturnsTrueForSameReference(Order a, Order b) {
        assertThat(a.equals(b), is(true));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "dummy"})
    void equalsReturnsFalseForNullOrDifferentClass(Object other) {
        var order = builder().build();

        assertThat(order.equals(other), is(false));
    }

    @Test
    void equalsReturnsTrueForEqualOrders() {
        var order1 = builder().build();
        var order2 = builder().build();

        assertThat(order1.equals(order2), is(true));
    }

    @Test
    void equalsReturnsFalseForDifferentId() {
        var order1 = builder().build();
        var order2 = builder().withId(OrderId.of(999L)).build();

        assertThat(order1.equals(order2), is(false));
    }

    @Test
    void hashCodeIsEqualForEqualOrders() {
        var order1 = builder().build();
        var order2 = builder().build();

        assertThat(order1.hashCode(), is(order2.hashCode()));
    }

    @Test
    void hashCodeIsDifferentForDifferentOrders() {
        var order1 = builder().build();
        var order2 = builder().withId(OrderId.of(999L)).build();

        assertThat(order1.hashCode(), not(order2.hashCode()));
    }
}
