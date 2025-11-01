package com.danielpgbrasil.orderprocessing.ut.application.order;

import com.danielpgbrasil.orderprocessing.application.order.StartOrderPickingUseCase;
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

class StartOrderPickingUseCaseTest {

    private Order order;
    private AppTransaction transaction;
    private OrderRepository repository;
    private StartOrderPickingUseCase useCase;

    @BeforeEach
    void beforeEach() {
        order = mock(Order.class);
        transaction = AppTransactionFixture.mockedTransaction();
        repository = mock(OrderRepository.class);
        useCase = new StartOrderPickingUseCase(transaction, repository);

        when(order.status()).thenReturn(OrderStatus.CREATED);
        when(repository.getOrThrow(ORDER_ID)).thenReturn(order);
        assertThatInTransaction(transaction).when(repository).save(any());
    }

    @Test
    void startsPickingWhenOrderIsCreated() {
        useCase.startPicking(ORDER_ID);

        verify(order).status();
        verify(order).startPicking();
        verify(repository).getOrThrow(ORDER_ID);
        verify(repository).save(order);
        verifyNoMoreInteractions(order, repository);
    }

    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, names = "CREATED", mode = EnumSource.Mode.EXCLUDE)
    void ignoresWhenOrderIsNotCreated(OrderStatus status) {
        when(order.status()).thenReturn(status);

        useCase.startPicking(ORDER_ID);

        verify(order).status();
        verify(repository).getOrThrow(ORDER_ID);
        verifyNoMoreInteractions(order, repository);
    }

    @Test
    void propagatesExceptionWhenRepositoryFails() {
        when(repository.getOrThrow(ORDER_ID)).thenThrow(RuntimeException.class);

        assertThrows(RuntimeException.class, () -> useCase.startPicking(ORDER_ID));

        verify(order, never()).startPicking();
        verify(repository, never()).save(any());
    }

    @Test
    void propagatesExceptionWhenTransactionFails() {
        doThrow(RuntimeException.class).when(transaction).execute(any());

        assertThrows(RuntimeException.class, () -> useCase.startPicking(ORDER_ID));

        verify(repository, never()).save(any());
        verify(order, never()).startPicking();
    }
}
