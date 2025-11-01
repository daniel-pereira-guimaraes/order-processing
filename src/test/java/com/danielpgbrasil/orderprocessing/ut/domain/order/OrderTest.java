package com.danielpgbrasil.orderprocessing.ut.domain.order;

import com.danielpgbrasil.orderprocessing.domain.order.Order;
import com.danielpgbrasil.orderprocessing.domain.order.OrderId;
import com.danielpgbrasil.orderprocessing.domain.order.OrderListener;
import com.danielpgbrasil.orderprocessing.domain.order.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.util.stream.Stream;

import static com.danielpgbrasil.orderprocessing.fixture.OrderFixture.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class OrderTest {

    private OrderListener listener;

    @BeforeEach
    void beforeEach() {
        listener = mock(OrderListener.class);
    }

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
    void builderThrowsExceptionWhenListenerNull() {
        var builder = builder().withListener(null);

        var exception = assertThrows(IllegalArgumentException.class, builder::build);

        assertThat(exception.getMessage(), is("O listener é requerido."));
    }

    @ParameterizedTest
    @EnumSource(OrderStatus.class)
    void finalizeCreationSetsIdAndStatusAndNotifiesListener(OrderStatus initialStatus) {
        var order = builder().withId(null)
                .withStatus(initialStatus)
                .withListener(listener)
                .build();

        order.finalizeCreation(ORDER_ID);

        assertThat(order.id(), is(ORDER_ID));
        assertThat(order.status(), is(OrderStatus.CREATED));
        verify(listener).statusChanged(order);
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
    void startPickingChangesStatusAndNotifiesListener() {
        var order = builder()
                .withStatus(OrderStatus.CREATED)
                .withListener(listener)
                .build();

        order.startPicking();

        assertThat(order.status(), is(OrderStatus.PICKING));
        verify(listener).statusChanged(order);
    }

    @Test
    void startPickingThrowsIfStatusNotCreated() {
        var order = builder().withStatus(OrderStatus.PICKING).build();

        var exception = assertThrows(IllegalStateException.class, order::startPicking);

        assertThat(exception.getMessage(), is("Status inválido: esperado CREATED, mas está PICKING."));
    }

    @Test
    void startTransitChangesStatusAndNotifiesListener() {
        var order = builder()
                .withStatus(OrderStatus.PICKING)
                .withListener(listener)
                .build();

        order.startTransit();

        assertThat(order.status(), is(OrderStatus.IN_TRANSIT));
        verify(listener).statusChanged(order);
    }

    @Test
    void startTransitThrowsIfStatusNotPicking() {
        var order = builder().withStatus(OrderStatus.CREATED).build();

        var exception = assertThrows(IllegalStateException.class, order::startTransit);

        assertThat(exception.getMessage(), is("Status inválido: esperado PICKING, mas está CREATED."));
    }

    @Test
    void markDeliveredChangesStatusAndNotifiesListener() {
        var order = builder()
                .withStatus(OrderStatus.IN_TRANSIT)
                .withListener(listener)
                .build();

        order.markDelivered();

        assertThat(order.status(), is(OrderStatus.DELIVERED));
        verify(listener).statusChanged(order);
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
