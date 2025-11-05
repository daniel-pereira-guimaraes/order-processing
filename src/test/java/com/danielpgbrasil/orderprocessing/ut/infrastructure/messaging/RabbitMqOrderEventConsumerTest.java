package com.danielpgbrasil.orderprocessing.ut.infrastructure.messaging;

import com.danielpgbrasil.orderprocessing.application.metrics.OrderMetrics;
import com.danielpgbrasil.orderprocessing.application.order.MarkOrderDeliveredService;
import com.danielpgbrasil.orderprocessing.application.order.StartOrderPickingService;
import com.danielpgbrasil.orderprocessing.application.order.StartOrderTransitService;
import com.danielpgbrasil.orderprocessing.domain.order.event.OrderEventType;
import com.danielpgbrasil.orderprocessing.domain.shared.TimeMillis;
import com.danielpgbrasil.orderprocessing.fixture.OrderEventFixture;
import com.danielpgbrasil.orderprocessing.infrastructure.messaging.RabbitMqOrderEventConsumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RabbitMqOrderEventConsumerTest {

    private StartOrderPickingService pickingService;
    private StartOrderTransitService transitService;
    private MarkOrderDeliveredService deliveryService;
    private OrderMetrics orderMetrics;
    private RabbitMqOrderEventConsumer consumer;

    @BeforeEach
    void beforeEach() {
        pickingService = mock(StartOrderPickingService.class);
        transitService = mock(StartOrderTransitService.class);
        deliveryService = mock(MarkOrderDeliveredService.class);
        orderMetrics = mock(OrderMetrics.class);

        consumer = new RabbitMqOrderEventConsumer(
                pickingService, transitService, deliveryService, orderMetrics);
    }

    @Test
    void processesCreatedEventSuccessfully() {
        var event = OrderEventFixture.builder()
                .withType(OrderEventType.CREATED)
                .build();

        consumer.handleOrderEvent(event);

        verify(pickingService).startPicking(event.orderId());
        verifyNoInteractions(transitService, deliveryService, orderMetrics);
    }

    @Test
    void processesPickingStartedEventSuccessfully() {
        var event = OrderEventFixture.builder()
                .withType(OrderEventType.PICKING_STARTED)
                .build();

        consumer.handleOrderEvent(event);

        verify(transitService).startTransit(event.orderId());
        verifyNoInteractions(pickingService, deliveryService, orderMetrics);
    }

    @Test
    void processesTransitStartedEventSuccessfully() {
        var event = OrderEventFixture.builder()
                .withType(OrderEventType.TRANSIT_STARTED)
                .build();

        consumer.handleOrderEvent(event);

        verify(deliveryService).markDelivered(event.orderId());
        verifyNoInteractions(pickingService, transitService, orderMetrics);
    }

    @Test
    void ignoresUnknownEventType() {
        var event = OrderEventFixture.builder()
                .withType(OrderEventType.DELIVERED)
                .build();

        consumer.handleOrderEvent(event);

        verifyNoInteractions(pickingService, transitService, deliveryService, orderMetrics);
    }

    @Test
    void throwsExceptionWhenCreatedAtIsZero() {
        var event = OrderEventFixture.builder()
                .withCreatedAt(TimeMillis.of(0L))
                .build();

        assertThrows(IllegalStateException.class, () -> consumer.handleOrderEvent(event));

        verify(orderMetrics).incrementFailedEvents();
    }

    @Test
    void incrementsFailedEventsWhenPickingServiceThrowsException() {
        var event = OrderEventFixture.builder()
                .withType(OrderEventType.CREATED)
                .build();

        doThrow(RuntimeException.class).when(pickingService).startPicking(any());

        assertThrows(RuntimeException.class, () -> consumer.handleOrderEvent(event));
        verify(orderMetrics).incrementFailedEvents();
    }

    @Test
    void incrementsFailedEventsWhenTransitServiceThrowsException() {
        var event = OrderEventFixture.builder()
                .withType(OrderEventType.PICKING_STARTED)
                .build();

        doThrow(RuntimeException.class).when(transitService).startTransit(any());

        assertThrows(RuntimeException.class, () -> consumer.handleOrderEvent(event));
        verify(orderMetrics).incrementFailedEvents();
    }

    @Test
    void incrementsFailedEventsWhenDeliveryServiceThrowsException() {
        var event = OrderEventFixture.builder()
                .withType(OrderEventType.TRANSIT_STARTED)
                .build();

        doThrow(RuntimeException.class).when(deliveryService).markDelivered(any());

        assertThrows(RuntimeException.class, () -> consumer.handleOrderEvent(event));
        verify(orderMetrics).incrementFailedEvents();
    }
}
