package com.danielpgbrasil.orderprocessing.ut.domain.order.event;

import com.danielpgbrasil.orderprocessing.domain.order.OrderStatus;
import com.danielpgbrasil.orderprocessing.domain.order.event.OrderEventType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

class OrderEventTypeTest {

    @Test
    void fromStatusReturnsCorrectEventType() {
        assertThat(OrderEventType.fromStatus(OrderStatus.CREATED), is(OrderEventType.CREATED));
        assertThat(OrderEventType.fromStatus(OrderStatus.PICKING), is(OrderEventType.PICKING_STARTED));
        assertThat(OrderEventType.fromStatus(OrderStatus.IN_TRANSIT), is(OrderEventType.TRANSIT_STARTED));
        assertThat(OrderEventType.fromStatus(OrderStatus.DELIVERED), is(OrderEventType.DELIVERED));
    }

    @ParameterizedTest
    @EnumSource(OrderStatus.class)
    void fromStatusReturnsEventTypeForAllStatus(OrderStatus status) {
        assertThat(OrderEventType.fromStatus(status), notNullValue());
    }
}
