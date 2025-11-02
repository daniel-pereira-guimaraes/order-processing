package com.danielpgbrasil.orderprocessing.it.infrastructure.jdbc;

import com.danielpgbrasil.orderprocessing.domain.order.*;
import com.danielpgbrasil.orderprocessing.infrastructure.jdbc.JdbcOrderRepository;
import com.danielpgbrasil.orderprocessing.it.infrastructure.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

import static com.danielpgbrasil.orderprocessing.fixture.OrderFixture.builder;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class JdbcOrderRepositoryTest extends IntegrationTestBase {

    private static final String NON_EXISTENT_ID_MESSAGE = "Pedido não encontrado: 999";

    @Autowired
    private JdbcOrderRepository repository;

    @Test
    void saveAndGetByIdSuccessfully() {
        var order = builder().withId(null).build();
        repository.save(order);

        var result = repository.get(order.id()).orElseThrow();

        assertThat(result.id(), notNullValue());
        assertThat(result, is(order));
    }

    @Test
    void getByIdReturnsEmptyWhenNotFound() {
        var id = OrderId.of(999L);

        var order = repository.get(id);

        assertThat(order.isEmpty(), is(true));
    }

    @Test
    void getOrThrowByIdThrowsExceptionWhenNotFound() {
        var id = OrderId.of(999L);

        var exception = assertThrows(OrderNotFoundException.class,
                () -> repository.getOrThrow(id)
        );

        assertThat(exception.getMessage(), is(NON_EXISTENT_ID_MESSAGE));
    }

    @Test
    void mustUpdateExistingOrder() {
        var originalOrder = createOriginalOrder();
        repository.save(originalOrder);

        var updatedOrder = createUpdatedOrder(originalOrder.id());
        repository.save(updatedOrder);

        var reloaded = repository.get(originalOrder.id()).orElseThrow();

        assertThat(reloaded.id(), is(originalOrder.id()));
        assertThat(reloaded.details().customerName(), is("Updated Customer"));
        assertThat(reloaded.details().customerAddress(), is("Updated Address"));
        assertThat(reloaded.details().items().size(), is(1));
        assertThat(reloaded.details().items().getFirst().productId(), is(99L));
        assertThat(reloaded.details().items().getFirst().quantity(), is(10));
        assertThat(reloaded.details().items().getFirst().price(), is(new BigDecimal("99.99")));
        assertThat(reloaded.status(), is(OrderStatus.PICKING));
    }

    private Order createOriginalOrder() {
        return builder()
                .withId(null)
                .withDetails(OrderDetails.builder()
                        .withCustomerName("Original Customer")
                        .withCustomerAddress("Original Address")
                        .withItems(List.of(
                                OrderItem.builder()
                                        .withProductId(1L)
                                        .withQuantity(5)
                                        .withPrice(new BigDecimal("10.50"))
                                        .build()
                        ))
                        .build())
                .withStatus(OrderStatus.CREATED)
                .build();
    }

    private Order createUpdatedOrder(OrderId id) {
        return builder()
                .withId(id)
                .withDetails(OrderDetails.builder()
                        .withCustomerName("Updated Customer")
                        .withCustomerAddress("Updated Address")
                        .withItems(List.of(
                                OrderItem.builder()
                                        .withProductId(99L)
                                        .withQuantity(10)
                                        .withPrice(new BigDecimal("99.99"))
                                        .build()
                        ))
                        .build())
                .withStatus(OrderStatus.PICKING)
                .build();
    }
}
