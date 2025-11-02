package com.danielpgbrasil.orderprocessing.it.infrastructure.jdbc;

import com.danielpgbrasil.orderprocessing.domain.order.OrderId;
import com.danielpgbrasil.orderprocessing.domain.order.event.*;
import com.danielpgbrasil.orderprocessing.domain.shared.TimeMillis;
import com.danielpgbrasil.orderprocessing.infrastructure.jdbc.JdbcOrderEventRepository;
import com.danielpgbrasil.orderprocessing.it.infrastructure.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static com.danielpgbrasil.orderprocessing.fixture.OrderEventFixture.builder;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class JdbcOrderEventRepositoryTest extends IntegrationTestBase {

    public static final OrderEventId NON_EXISTENT_ID = OrderEventId.of(999L);
    @Autowired
    private JdbcOrderEventRepository repository;

    @Test
    void saveAndGetByIdSuccessfully() {
        var savedEvent = builder().withId(null).withOrderId(OrderId.of(1L)).build();
        repository.save(savedEvent);

        var retrievedEvent = repository.get(savedEvent.id()).orElseThrow();

        assertThat(retrievedEvent.id(), notNullValue());
        assertThat(retrievedEvent, is(savedEvent));
    }

    @Test
    void getByIdReturnsEmptyWhenNotFound() {
        var result = repository.get(NON_EXISTENT_ID);

        assertThat(result.isEmpty(), is(true));
    }

    @Test
    void getOrThrowByIdThrowsWhenNotFound() {
        var exception = assertThrows(OrderEventNotFoundException.class,
                () -> repository.getOrThrow(NON_EXISTENT_ID)
        );

        assertThat(exception.getMessage(), is("Evento de pedido não encontrado: 999"));
    }

    @Test
    void mustUpdateExistingEvent() {
        var original = OrderEvent.builder()
                .withId(null)
                .withOrderId(OrderId.of(1L))
                .withType(OrderEventType.CREATED)
                .withCreatedAt(TimeMillis.of(1000L))
                .withPublished(false)
                .build();
        repository.save(original);

        var updated = OrderEvent.builder()
                .withId(original.id())
                .withOrderId(OrderId.of(2L))
                .withType(OrderEventType.PICKING_STARTED)
                .withCreatedAt(TimeMillis.of(2000L))
                .withPublished(true)
                .build();
        repository.save(updated);

        var retrieved = repository.get(original.id()).orElseThrow();

        assertThat(retrieved, is(updated));
    }

    @Test
    void findByOrderIdReturnsCorrectEvents() {
        var expected = List.of(
                createEvent(2L, 1L, OrderEventType.PICKING_STARTED, 1700000005000L, true),
                createEvent(3L, 1L, OrderEventType.TRANSIT_STARTED, 1700000010000L, true),
                createEvent(9L, 1L, OrderEventType.CREATED, 1700000000000L, false)
        );

        var retrieved = repository.findByOrderId(OrderId.of(1L));

        assertThat(retrieved, is(expected));
    }
    @Test
    void findAllUnpublishedReturnsOnlyUnpublishedEvents() {
        var expected = List.of(
                createEvent(1L, 4L, OrderEventType.CREATED, 1700000300000L, false),
                createEvent(4L, 2L, OrderEventType.CREATED, 1700000100000L, false),
                createEvent(6L, 3L, OrderEventType.CREATED, 1700000200000L, false),
                createEvent(8L, 3L, OrderEventType.DELIVERED, 1700000208000L, false),
                createEvent(9L, 1L, OrderEventType.CREATED, 1700000000000L, false)
        );

        var retrieved = repository.findAllUnpublished();

        assertThat(retrieved, is(expected));
    }

    private OrderEvent createEvent(Long id, Long orderId, OrderEventType type,
                                   long createdAt, boolean published) {
        return OrderEvent.builder()
                .withId(OrderEventId.ofNullable(id).orElse(null))
                .withOrderId(OrderId.of(orderId))
                .withType(type)
                .withCreatedAt(TimeMillis.of(createdAt))
                .withPublished(published)
                .build();
    }
}
