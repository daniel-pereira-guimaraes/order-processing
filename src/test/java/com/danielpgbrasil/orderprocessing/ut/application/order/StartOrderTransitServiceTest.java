package com.danielpgbrasil.orderprocessing.ut.application.order;

import com.danielpgbrasil.orderprocessing.application.order.StartOrderTransitService;
import com.danielpgbrasil.orderprocessing.application.shared.AppTransaction;
import com.danielpgbrasil.orderprocessing.domain.order.*;
import com.danielpgbrasil.orderprocessing.fixture.AppTransactionFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static com.danielpgbrasil.orderprocessing.fixture.AppTransactionFixture.assertThatInTransaction;
import static com.danielpgbrasil.orderprocessing.fixture.OrderFixture.ORDER_ID;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class StartOrderTransitServiceTest {

    private Order order;
    private AppTransaction transaction;
    private OrderRepository repository;
    private StartOrderTransitService service;

    @BeforeEach
    void beforeEach() {
        order = mock(Order.class);
        transaction = AppTransactionFixture.mockedTransaction();
        repository = mock(OrderRepository.class);
        service = new StartOrderTransitService(transaction, repository);

        when(repository.getOrThrow(ORDER_ID)).thenReturn(order);
        when(order.status()).thenReturn(OrderStatus.PICKING);
        assertThatInTransaction(transaction).when(repository).save(any());
    }

    @Test
    void startsTransitWhenOrderIsPicking() {
        service.startTransit(ORDER_ID);

        verify(repository).getOrThrow(ORDER_ID);
        verify(order).status();
        verify(order).startTransit();
        verify(repository).save(order);
        verifyNoMoreInteractions(order, repository);
    }

    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, names = "PICKING", mode = EnumSource.Mode.EXCLUDE)
    void ignoresWhenOrderIsNotPicking(OrderStatus status) {
        when(order.status()).thenReturn(status);

        service.startTransit(ORDER_ID);

        verify(repository).getOrThrow(ORDER_ID);
        verify(order).status();
        verifyNoMoreInteractions(order, repository);
    }

    @Test
    void propagatesExceptionWhenRepositoryFails() {
        when(repository.getOrThrow(ORDER_ID)).thenThrow(RuntimeException.class);

        assertThrows(RuntimeException.class, () -> service.startTransit(ORDER_ID));

        verify(repository).getOrThrow(ORDER_ID);
        verifyNoMoreInteractions(repository, order);
    }

    @Test
    void propagatesExceptionWhenTransactionFails() {
        doThrow(RuntimeException.class).when(transaction).execute(any());

        assertThrows(RuntimeException.class, () -> service.startTransit(ORDER_ID));

        verifyNoInteractions(repository, order);
    }
}
