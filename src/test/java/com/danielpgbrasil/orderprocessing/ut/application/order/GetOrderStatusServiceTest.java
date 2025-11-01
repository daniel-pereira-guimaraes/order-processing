package com.danielpgbrasil.orderprocessing.ut.application.order;

import static com.danielpgbrasil.orderprocessing.fixture.OrderFixture.ORDER_ID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.danielpgbrasil.orderprocessing.application.order.GetOrderStatusService;
import com.danielpgbrasil.orderprocessing.domain.order.*;

import com.danielpgbrasil.orderprocessing.fixture.OrderFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class GetOrderStatusServiceTest {

    private OrderRepository repository;
    private GetOrderStatusService service;

    @BeforeEach
    void beforeEach() {
        repository = mock(OrderRepository.class);
        service = new GetOrderStatusService(repository);
    }

    @ParameterizedTest
    @EnumSource(OrderStatus.class)
    void retrievesOrderStatusSuccessfully(OrderStatus status) {
        var order = OrderFixture.builder().withStatus(status).build();
        when(repository.getOrThrow(order.id())).thenReturn(order);

        var result = service.getStatus(order.id());

        assertThat(result, is(status));
        verify(repository).getOrThrow(order.id());
    }

    @Test
    void propagatesExceptionWhenRepositoryFails() {
        when(repository.getOrThrow(ORDER_ID)).thenThrow(RuntimeException.class);

        assertThrows(RuntimeException.class, () -> service.getStatus(ORDER_ID));

        verify(repository).getOrThrow(ORDER_ID);
    }
}
