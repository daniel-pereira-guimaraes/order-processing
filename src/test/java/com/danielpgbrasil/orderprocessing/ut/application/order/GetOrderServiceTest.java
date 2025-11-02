package com.danielpgbrasil.orderprocessing.ut.application.order;

import static com.danielpgbrasil.orderprocessing.fixture.OrderFixture.ORDER_ID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.danielpgbrasil.orderprocessing.application.order.GetOrderService;
import com.danielpgbrasil.orderprocessing.domain.order.OrderRepository;
import com.danielpgbrasil.orderprocessing.domain.order.event.OrderEvent;
import com.danielpgbrasil.orderprocessing.domain.order.event.OrderEventRepository;
import com.danielpgbrasil.orderprocessing.fixture.OrderFixture;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

class GetOrderServiceTest {

    private OrderRepository orderRepository;
    private OrderEventRepository eventRepository;
    private GetOrderService service;

    @BeforeEach
    void beforeEach() {
        orderRepository = mock(OrderRepository.class);
        eventRepository = mock(OrderEventRepository.class);
        service = new GetOrderService(orderRepository, eventRepository);
    }

    @Test
    void retrievesOrderWithoutEventsWhenIncludeEventsIsFalse() {
        var expectedOrder = OrderFixture.builder().build();
        when(orderRepository.getOrThrow(expectedOrder.id())).thenReturn(expectedOrder);

        var result = service.getOrder(expectedOrder.id(), false);

        assertThat(result.order(), is(expectedOrder));
        assertThat(result.events(), is(empty()));

        verify(orderRepository).getOrThrow(expectedOrder.id());
        verifyNoInteractions(eventRepository);
    }

    @Test
    void retrievesOrderWithEventsWhenIncludeEventsIsTrue() {
        var expectedOrder = OrderFixture.builder().build();
        var expectedEvents = List.of(mock(OrderEvent.class), mock(OrderEvent.class));

        when(orderRepository.getOrThrow(expectedOrder.id())).thenReturn(expectedOrder);
        when(eventRepository.findByOrderId(expectedOrder.id())).thenReturn(expectedEvents);

        var result = service.getOrder(expectedOrder.id(), true);

        assertThat(result.order(), is(expectedOrder));
        assertThat(result.events(), is(expectedEvents));

        verify(orderRepository).getOrThrow(expectedOrder.id());
        verify(eventRepository).findByOrderId(expectedOrder.id());
    }

    @Test
    void propagatesExceptionWhenOrderRepositoryFails() {
        when(orderRepository.getOrThrow(ORDER_ID)).thenThrow(RuntimeException.class);

        assertThrows(RuntimeException.class, () -> service.getOrder(ORDER_ID, true));

        verify(orderRepository).getOrThrow(ORDER_ID);
        verifyNoInteractions(eventRepository);
    }
}
