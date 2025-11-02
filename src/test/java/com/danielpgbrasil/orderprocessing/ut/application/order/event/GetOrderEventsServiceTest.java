package com.danielpgbrasil.orderprocessing.ut.application.order.event;

import static com.danielpgbrasil.orderprocessing.fixture.OrderFixture.ORDER_ID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.danielpgbrasil.orderprocessing.application.order.event.GetOrderEventsService;
import com.danielpgbrasil.orderprocessing.domain.order.OrderNotFoundException;
import com.danielpgbrasil.orderprocessing.domain.order.OrderRepository;
import com.danielpgbrasil.orderprocessing.domain.order.event.OrderEvent;
import com.danielpgbrasil.orderprocessing.domain.order.event.OrderEventRepository;
import com.danielpgbrasil.orderprocessing.fixture.OrderEventFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

class GetOrderEventsServiceTest {

    private static final List<OrderEvent> EXPECTED_EVENTS = List.of(
            OrderEventFixture.builder().build()
    );

    private OrderRepository orderRepository;
    private OrderEventRepository orderEventRepository;
    private GetOrderEventsService service;

    @BeforeEach
    void beforeEach() {
        orderRepository = mock(OrderRepository.class);
        orderEventRepository = mock(OrderEventRepository.class);

        service = new GetOrderEventsService(orderRepository, orderEventRepository);

        when(orderRepository.exists(ORDER_ID)).thenReturn(true);
        when(orderEventRepository.findByOrderId(ORDER_ID)).thenReturn(EXPECTED_EVENTS);
    }

    @Test
    void getsEventsSuccessfully() {
        var events = service.getEvents(ORDER_ID);

        assertThat(events, is(EXPECTED_EVENTS));
        verify(orderRepository).exists(ORDER_ID);
        verify(orderEventRepository).findByOrderId(ORDER_ID);
    }

    @Test
    void propagatesOrderNotFoundExceptionWhenOrderDoesNotExist() {
        when(orderRepository.exists(ORDER_ID)).thenReturn(false);

        assertThrows(OrderNotFoundException.class,
                () -> service.getEvents(ORDER_ID)
        );

        verify(orderRepository).exists(ORDER_ID);
        verifyNoInteractions(orderEventRepository);
    }

    @Test
    void propagatesRuntimeExceptionWhenRepositoryFails() {
        when(orderEventRepository.findByOrderId(ORDER_ID)).thenThrow(RuntimeException.class);

        assertThrows(RuntimeException.class, () -> service.getEvents(ORDER_ID));

        verify(orderRepository).exists(ORDER_ID);
        verify(orderEventRepository).findByOrderId(ORDER_ID);
    }
}
