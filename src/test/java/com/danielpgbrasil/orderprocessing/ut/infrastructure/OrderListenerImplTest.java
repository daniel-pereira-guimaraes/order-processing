package com.danielpgbrasil.orderprocessing.ut.infrastructure;

import com.danielpgbrasil.orderprocessing.domain.order.Order;
import com.danielpgbrasil.orderprocessing.domain.order.OrderStatus;
import com.danielpgbrasil.orderprocessing.domain.order.event.OrderEventType;
import com.danielpgbrasil.orderprocessing.application.order.event.CreateOrderEventService;
import com.danielpgbrasil.orderprocessing.infrastructure.OrderListenerImpl;
import com.danielpgbrasil.orderprocessing.infrastructure.messaging.AsyncPublishPendingOrderEventsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderListenerImplTest {

    private CreateOrderEventService service;
    private AsyncPublishPendingOrderEventsService asyncPublishPendingOrderEventsService;
    private OrderListenerImpl listener;

    @BeforeEach
    void beforeEach() {
        service = mock(CreateOrderEventService.class);
        asyncPublishPendingOrderEventsService = mock(AsyncPublishPendingOrderEventsService.class);
        listener = new OrderListenerImpl(service, asyncPublishPendingOrderEventsService);
    }

    @ParameterizedTest
    @CsvSource({
            "CREATED, CREATED",
            "PICKING, PICKING_STARTED",
            "IN_TRANSIT, TRANSIT_STARTED",
            "DELIVERED, DELIVERED"
    })
    void statusChangedCallsServiceWithCorrectEventType(String statusName,
                                                       String expectedEventTypeName) {
        var status = OrderStatus.valueOf(statusName);
        var expectedEventType = OrderEventType.valueOf(expectedEventTypeName);
        var order = mock(Order.class);
        when(order.status()).thenReturn(status);

        listener.statusChanged(order);

        verify(service).createEvent(order, expectedEventType);
        verify(asyncPublishPendingOrderEventsService).execute();
    }

    @Test
    void statusChangedPropagatesExceptionFromService() {
        var order = mock(Order.class);
        when(order.status()).thenReturn(OrderStatus.CREATED);
        doThrow(new RuntimeException("Service failed"))
                .when(service).createEvent(order, OrderEventType.CREATED);

        assertThrows(RuntimeException.class, () -> listener.statusChanged(order));
    }
}
