package com.danielpgbrasil.orderprocessing.ut.domain.order.event;

import com.danielpgbrasil.orderprocessing.domain.order.OrderId;
import com.danielpgbrasil.orderprocessing.domain.order.event.OrderEvent;
import com.danielpgbrasil.orderprocessing.domain.order.event.OrderEventId;
import com.danielpgbrasil.orderprocessing.domain.order.event.OrderEventType;
import com.danielpgbrasil.orderprocessing.domain.shared.TimeMillis;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.util.stream.Stream;

import static com.danielpgbrasil.orderprocessing.fixture.OrderEventFixture.*;
import static com.danielpgbrasil.orderprocessing.fixture.OrderFixture.ORDER_ID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderEventTest {

    @Test
    void builderCreatesOrderEventWhenValid() {
        var event = builder().build();

        assertThat(event.id(), is(ORDER_EVENT_ID));
        assertThat(event.orderId(), is(ORDER_ID));
        assertThat(event.type(), is(OrderEventType.CREATED));
        assertThat(event.createdAt(), is(CREATED_AT));
        assertThat(event.isPublished(), is(false));
    }

    @Test
    void builderThrowsExceptionWhenOrderIdNull() {
        var builder = builder().withOrderId(null);

        var exception = assertThrows(IllegalArgumentException.class, builder::build);

        assertThat(exception.getMessage(), is("O id do pedido é requerido."));
    }

    @Test
    void builderThrowsExceptionWhenTypeNull() {
        var builder = builder().withType(null);

        var exception = assertThrows(IllegalArgumentException.class, builder::build);

        assertThat(exception.getMessage(), is("O tipo do evento é requerido."));
    }

    @Test
    void builderThrowsExceptionWhenCreatedAtNull() {
        var builder = builder().withCreatedAt(null);

        var exception = assertThrows(IllegalArgumentException.class, builder::build);

        assertThat(exception.getMessage(), is("O timestamp do evento é requerido."));
    }

    @Test
    void builderThrowsExceptionWhenPublishedNull() {
        var builder = builder().withPublished(null);

        var exception = assertThrows(IllegalArgumentException.class, builder::build);

        assertThat(exception.getMessage(), is("Published é requerido."));
    }

    @Test
    void finalizeCreationSetsIdWhenNotPreviouslySet() {
        var event = builder().withId(null).build();
        var newId = OrderEventId.of(999L);

        event.finalizeCreation(newId);

        assertThat(event.id(), is(newId));
    }

    @Test
    void finalizeCreationThrowsIfAlreadyFinalized() {
        var event = builder().build();
        var newEventId = OrderEventId.of(999L);

        var exception = assertThrows(IllegalStateException.class,
                () -> event.finalizeCreation(newEventId)
        );

        assertThat(exception.getMessage(), is("A criação do evento já foi finalizada."));
    }

    @Test
    void markAsPublishedChangePublishedToTrue() {
        var event = builder().withPublished(false).build();

        event.markAsPublished();

        assertThat(event.isPublished(), is(true));
    }

    @Test
    void markAsPublishedThrowsWhenAlreadyPublished() {
        var event = builder().withPublished(true).build();

        var exception = assertThrows(IllegalStateException.class, event::markAsPublished);

        assertThat(exception.getMessage(), is("Evento já publicado."));
    }

    static Stream<Arguments> sameReferenceProvider() {
        var event = builder().build();
        return Stream.of(Arguments.of(event, event));
    }

    @ParameterizedTest
    @MethodSource("sameReferenceProvider")
    void equalsReturnsTrueForSameReference(OrderEvent a, OrderEvent b) {
        assertThat(a.equals(b), is(true));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "dummy"})
    void equalsReturnsFalseForNullOrDifferentClass(Object other) {
        var event = builder().build();
        assertThat(event.equals(other), is(false));
    }

    @Test
    void equalsReturnsTrueForEqualEvents() {
        var event1 = builder().withCreatedAt(CREATED_AT).build();
        var event2 = builder().withCreatedAt(CREATED_AT).build();

        assertThat(event1.equals(event2), is(true));
        assertThat(event1.hashCode(), is(event2.hashCode()));
    }

    @Test
    void equalsReturnsFalseForDifferentOrderId() {
        var event1 = builder().build();
        var event2 = builder().withOrderId(OrderId.of(999L)).build();

        assertThat(event1.equals(event2), is(false));
        assertThat(event1.hashCode(), not(event2.hashCode()));
    }

    @Test
    void equalsReturnsFalseForDifferentType() {
        var event1 = builder().build();
        var event2 = builder().withType(OrderEventType.DELIVERED).build();

        assertThat(event1.equals(event2), is(false));
        assertThat(event1.hashCode(), not(event2.hashCode()));
    }

    @Test
    void equalsReturnsFalseForDifferentCreatedAt() {
        var event1 = builder().withCreatedAt(TimeMillis.of(1000L)).build();
        var event2 = builder().withCreatedAt(TimeMillis.of(2000L)).build();

        assertThat(event1.equals(event2), is(false));
        assertThat(event1.hashCode(), not(event2.hashCode()));
    }

    @Test
    void equalsReturnsFalseForDifferentPublished() {
        var event1 = builder().withCreatedAt(CREATED_AT).withPublished(false).build();
        var event2 = builder().withCreatedAt(CREATED_AT).withPublished(true).build();

        assertThat(event1.equals(event2), is(false));
        assertThat(event1.hashCode(), not(event2.hashCode()));
    }

}
