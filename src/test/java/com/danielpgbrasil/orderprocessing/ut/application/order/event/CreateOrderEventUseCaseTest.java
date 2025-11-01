package com.danielpgbrasil.orderprocessing.ut.application.order.event;

import static com.danielpgbrasil.orderprocessing.fixture.AppTransactionFixture.assertThatInTransaction;
import static com.danielpgbrasil.orderprocessing.fixture.AppTransactionFixture.mockedTransaction;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.danielpgbrasil.orderprocessing.application.order.event.CreateOrderEventUseCase;
import com.danielpgbrasil.orderprocessing.application.shared.AppTransaction;
import com.danielpgbrasil.orderprocessing.domain.order.Order;
import com.danielpgbrasil.orderprocessing.domain.order.event.OrderEvent;
import com.danielpgbrasil.orderprocessing.domain.order.event.OrderEventRepository;
import com.danielpgbrasil.orderprocessing.domain.order.event.OrderEventType;
import com.danielpgbrasil.orderprocessing.domain.shared.AppClock;

import com.danielpgbrasil.orderprocessing.fixture.OrderFixture;
import com.danielpgbrasil.orderprocessing.fixture.TimeMillisFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CreateOrderEventUseCaseTest {

    private AppTransaction transaction;
    private OrderEventRepository repository;
    private AppClock clock;
    private CreateOrderEventUseCase useCase;
    private Order order;

    @BeforeEach
    void beforeEach() {
        transaction = mockedTransaction();
        repository = mock(OrderEventRepository.class);
        clock = mock(AppClock.class);
        useCase = new CreateOrderEventUseCase(transaction, repository, clock);
        order = OrderFixture.builder().build();

        when(clock.now()).thenReturn(TimeMillisFixture.NOW);
        assertThatInTransaction(transaction).when(repository).save(any());
    }

    @Test
    void createsEventSuccessfully() {
        var event = useCase.createEvent(order, OrderEventType.CREATED);

        assertThat(event.orderId(), is(order.id()));
        assertThat(event.type(), is(OrderEventType.CREATED));
        assertThat(event.createdAt(), is(TimeMillisFixture.NOW));
        assertThat(event.isPublished(), is(false));

        verify(repository).save(event);
    }

    @Test
    void propagatesExceptionWhenRepositoryFails() {
        doThrow(RuntimeException.class).when(repository).save(any(OrderEvent.class));

        assertThrows(RuntimeException.class, () -> useCase.createEvent(order, OrderEventType.CREATED));

        verify(repository).save(any(OrderEvent.class));
    }

    @Test
    void propagatesExceptionWhenTransactionFails() {
        doThrow(RuntimeException.class).when(transaction).execute(any());

        assertThrows(RuntimeException.class, () -> useCase.createEvent(order, OrderEventType.CREATED));

        verify(repository, never()).save(any(OrderEvent.class));
    }

    @Test
    void propagatesExceptionWhenClockFails() {
        when(clock.now()).thenThrow(RuntimeException.class);

        assertThrows(RuntimeException.class, () -> useCase.createEvent(order, OrderEventType.CREATED));

        verify(repository, never()).save(any(OrderEvent.class));
    }
}
