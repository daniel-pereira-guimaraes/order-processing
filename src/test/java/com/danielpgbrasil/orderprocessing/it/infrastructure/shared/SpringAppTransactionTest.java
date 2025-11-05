package com.danielpgbrasil.orderprocessing.it.infrastructure.shared;

import com.danielpgbrasil.orderprocessing.application.shared.AppTransaction;
import com.danielpgbrasil.orderprocessing.domain.order.OrderId;
import com.danielpgbrasil.orderprocessing.domain.order.event.OrderEventRepository;
import com.danielpgbrasil.orderprocessing.fixture.OrderEventFixture;
import com.danielpgbrasil.orderprocessing.it.infrastructure.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class SpringAppTransactionTest extends IntegrationTestBase {

    @Autowired
    private AppTransaction appTransaction;

    @Autowired
    private OrderEventRepository repository;

    @Test
    void executeAddsEventWhenNoError() {
        var event = OrderEventFixture.builder()
                .withId(null)
                .withOrderId(OrderId.of(1L))
                .build();

        appTransaction.execute(() -> {
            repository.save(event);
            assertThat(appTransaction.inTransaction(), is(true));
        });

        assertThat(event.id(), notNullValue());
        assertThat(repository.get(event.id()).orElseThrow(), is(event));
        assertThat(appTransaction.inTransaction(), is(false));
    }

    @Test
    void executeDoesNotAddEventWhenExceptionOccurs() {
        var event = OrderEventFixture.builder()
                .withId(null)
                .withOrderId(OrderId.of(1L))
                .build();

        assertThrows(RuntimeException.class, () -> appTransaction.execute(() -> {
            repository.save(event);
            throw new RuntimeException("After INSERT, before COMMIT");
        }));

        assertThat(event.id(), notNullValue());
        assertThat(repository.get(event.id()).isEmpty(), is(true));
    }
}