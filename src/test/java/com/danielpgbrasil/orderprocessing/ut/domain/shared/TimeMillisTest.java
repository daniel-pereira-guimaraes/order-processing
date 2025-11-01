package com.danielpgbrasil.orderprocessing.ut.domain.shared;


import com.danielpgbrasil.orderprocessing.domain.shared.TimeMillis;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TimeMillisTest {

    @ParameterizedTest
    @ValueSource(longs = {Long.MIN_VALUE, -1L, 0L, 1L, Long.MAX_VALUE})
    void ofReturnsTimeMillisWhenValid(long value) {
        var time = TimeMillis.of(value);

        assertThat(time, notNullValue());
        assertThat(time.value(), is(value));
    }

    @Test
    void ofThrowsExceptionWhenValueIsNull() {
        assertThrows(NullPointerException.class, () -> TimeMillis.of(null));
    }

    @Test
    void ofNullableReturnsOptionalWithValueWhenValid() {
        var result = TimeMillis.ofNullable(10L);

        assertThat(result.isPresent(), is(true));
        assertThat(result.get().value(), is(10L));
    }

    @Test
    void ofNullableReturnsEmptyWhenValueIsNull() {
        var result = TimeMillis.ofNullable(null);

        assertThat(result.isEmpty(), is(true));
    }

    @Test
    void equalsAndHashCodeReturnsCorrectly() {
        var t1 = TimeMillis.of(10L);
        var t2 = TimeMillis.of(10L);
        var t3 = TimeMillis.of(11L);

        assertThat(t1, is(t2));
        assertThat(t1.hashCode(), is(t2.hashCode()));

        assertThat(t1, not(t3));
        assertThat(t1.hashCode(), not(t3.hashCode()));
    }

    @Test
    void nowReturnsNonNullValue() {
        var time = TimeMillis.now();

        assertThat(time, notNullValue());
        assertThat(time.value(), greaterThan(0L));
    }

    @Test
    void nowReturnsIncreasingValues() throws InterruptedException {
        var t1 = TimeMillis.now();
        Thread.sleep(1); //NOSONAR
        var t2 = TimeMillis.now();

        assertThat(t2.value(), is(greaterThan(t1.value())));
    }
}
