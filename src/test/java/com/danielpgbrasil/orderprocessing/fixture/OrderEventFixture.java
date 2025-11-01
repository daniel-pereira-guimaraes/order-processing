package com.danielpgbrasil.orderprocessing.fixture;

import com.danielpgbrasil.orderprocessing.domain.order.event.OrderEvent;
import com.danielpgbrasil.orderprocessing.domain.order.event.OrderEventId;
import com.danielpgbrasil.orderprocessing.domain.order.event.OrderEventType;
import com.danielpgbrasil.orderprocessing.domain.shared.TimeMillis;

import static com.danielpgbrasil.orderprocessing.fixture.OrderFixture.ORDER_ID;

public class OrderEventFixture {

    public static final OrderEventId ORDER_EVENT_ID = OrderEventId.of(456L);
    public static final TimeMillis CREATED_AT = TimeMillis.now();

    private OrderEventFixture() {
    }

    public static OrderEvent.Builder builder() {
        return OrderEvent.builder()
                .withId(ORDER_EVENT_ID)
                .withOrderId(ORDER_ID)
                .withType(OrderEventType.CREATED)
                .withCreatedAt(CREATED_AT)
                .withPublished(false);
    }
}
