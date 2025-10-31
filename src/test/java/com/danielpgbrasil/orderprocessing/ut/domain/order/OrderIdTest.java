package com.danielpgbrasil.orderprocessing.ut.domain.order;

import com.danielpgbrasil.orderprocessing.domain.order.OrderId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderIdTest {

    @ParameterizedTest
    @ValueSource(longs = {1L, Long.MAX_VALUE})
    void ofReturnsOrderIdWhenValid(long value) {
        var orderId = OrderId.of(value);

        assertThat(orderId, notNullValue());
        assertThat(orderId.value(), is(value));
    }

    @ParameterizedTest
    @ValueSource(longs = {0L, -1L, Long.MIN_VALUE})
    void ofThrowsExceptionWhenIdIsNotPositive(long invalidValue) {
        var exception = assertThrows(IllegalArgumentException.class, () ->
                OrderId.of(invalidValue)
        );
        assertThat(exception.getMessage(), is("O id deve ser positivo."));
    }

    @Test
    void ofThrowsExceptionWhenIdIsNull() {
        var exception = assertThrows(IllegalArgumentException.class, () ->
                OrderId.of(null)
        );
        assertThat(exception.getMessage(), is("O id é requerido."));
    }

    @Test
    void ofNullableReturnsOptionalWithValueWhenValid() {
        var result = OrderId.ofNullable(42L);

        assertThat(result.isPresent(), is(true));
        assertThat(result.get().value(), is(42L));
    }

    @Test
    void ofNullableReturnsEmptyWhenValueIsNull() {
        var result = OrderId.ofNullable(null);

        assertThat(result.isEmpty(), is(true));
    }

    @Test
    void equalsAndHashCodeShouldWorkCorrectly() {
        var id1 = OrderId.of(10L);
        var id2 = OrderId.of(10L);
        var id3 = OrderId.of(20L);

        assertThat(id1, is(id2));
        assertThat(id1.hashCode(), is(id2.hashCode()));

        assertThat(id1, not(id3));
        assertThat(id1.hashCode(), not(id3.hashCode()));
    }
}
