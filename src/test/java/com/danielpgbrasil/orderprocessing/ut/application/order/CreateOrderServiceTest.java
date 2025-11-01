package com.danielpgbrasil.orderprocessing.ut.application.order;

import static com.danielpgbrasil.orderprocessing.fixture.AppTransactionFixture.assertThatInTransaction;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.danielpgbrasil.orderprocessing.domain.order.*;
import com.danielpgbrasil.orderprocessing.fixture.AppTransactionFixture;
import com.danielpgbrasil.orderprocessing.fixture.OrderDetailsFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.danielpgbrasil.orderprocessing.application.order.CreateOrderService;
import com.danielpgbrasil.orderprocessing.application.shared.AppTransaction;

class CreateOrderServiceTest {

    private AppTransaction transaction;
    private OrderRepository repository;
    private OrderListener listener;
    private CreateOrderService service;
    private OrderDetails orderDetails;

    @BeforeEach
    void beforeEach() {
        transaction = AppTransactionFixture.mockedTransaction();
        repository = mock(OrderRepository.class);
        listener = mock(OrderListener.class);
        service = new CreateOrderService(transaction, repository, listener);
        orderDetails = OrderDetailsFixture.builder().build();

        assertThatInTransaction(transaction).when(repository).save(any());
    }

    @Test
    void createsOrderSuccessfully() {
        var order = service.createOrder(orderDetails);

        assertThat(order.details(), is(orderDetails));
        assertThat(order.status(), is(OrderStatus.CREATED));
        verify(repository).save(order);
        verify(listener, never()).statusChanged(any());
    }

    @Test
    void propagatesExceptionWhenRepositoryFails() {
        doThrow(RuntimeException.class).when(repository).save(any(Order.class));

        assertThrows(RuntimeException.class, () -> service.createOrder(orderDetails));

        verify(repository).save(any(Order.class));
    }

    @Test
    void propagatesExceptionWhenTransactionFails() {
        doThrow(RuntimeException.class).when(transaction).execute(any());

        assertThrows(RuntimeException.class, () -> service.createOrder(orderDetails));

        verify(repository, never()).save(any(Order.class));
    }
}
