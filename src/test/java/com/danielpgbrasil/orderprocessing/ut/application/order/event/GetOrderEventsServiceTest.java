package com.danielpgbrasil.orderprocessing.ut.application.order.event;

import static com.danielpgbrasil.orderprocessing.fixture.OrderFixture.ORDER_ID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.danielpgbrasil.orderprocessing.application.order.event.GetOrderEventsService;
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

    private OrderEventRepository repository;
    private GetOrderEventsService service;

    @BeforeEach
    void beforeEach() {
        repository = mock(OrderEventRepository.class);
        service = new GetOrderEventsService(repository);

        when(repository.findByOrderId(ORDER_ID)).thenReturn(EXPECTED_EVENTS);
    }

    @Test
    void getsEventsSuccessfully() {
        var events = service.getEvents(ORDER_ID);

        assertThat(events, is(EXPECTED_EVENTS));
        verify(repository).findByOrderId(ORDER_ID);
    }

    @Test
    void propagatesExceptionWhenRepositoryFails() {
        when(repository.findByOrderId(ORDER_ID)).thenThrow(RuntimeException.class);

        assertThrows(RuntimeException.class, () -> service.getEvents(ORDER_ID));

        verify(repository).findByOrderId(ORDER_ID);
    }
}
