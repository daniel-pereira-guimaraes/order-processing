package com.danielpgbrasil.orderprocessing.ut.application.order.event;

import com.danielpgbrasil.orderprocessing.application.metrics.OrderMetrics;
import com.danielpgbrasil.orderprocessing.application.order.event.OrderEventPublisher;
import com.danielpgbrasil.orderprocessing.application.order.event.PublishPendingOrderEventsService;
import com.danielpgbrasil.orderprocessing.application.shared.AppTransaction;
import com.danielpgbrasil.orderprocessing.domain.order.OrderId;
import com.danielpgbrasil.orderprocessing.domain.order.event.OrderEvent;
import com.danielpgbrasil.orderprocessing.domain.order.event.OrderEventId;
import com.danielpgbrasil.orderprocessing.domain.order.event.OrderEventRepository;
import com.danielpgbrasil.orderprocessing.domain.order.event.OrderEventType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.danielpgbrasil.orderprocessing.fixture.AppTransactionFixture.assertThatInTransaction;
import static com.danielpgbrasil.orderprocessing.fixture.AppTransactionFixture.mockedTransaction;
import static org.mockito.Mockito.*;

class PublishPendingOrderEventsServiceTest {

    private OrderEvent event1;
    private OrderEvent event2;

    private AppTransaction transaction;
    private OrderEventRepository repository;
    private OrderEventPublisher publisher;
    private OrderMetrics orderMetrics;
    private PublishPendingOrderEventsService service;

    @BeforeEach
    void beforeEach() {
        event1 = mockOrderEvent(1L);
        event2 = mockOrderEvent(2L);

        transaction = mockedTransaction();
        repository = mock(OrderEventRepository.class);
        publisher = mock(OrderEventPublisher.class);
        orderMetrics = mock(OrderMetrics.class);
        service = new PublishPendingOrderEventsService(transaction, repository, publisher, orderMetrics);

        assertThatInTransaction(transaction).when(repository).save(any());
        assertThatInTransaction(transaction).when(publisher).publish(any());

        when(repository.findAllUnpublished()).thenReturn(List.of(event1, event2));
    }

    @Test
    void publishesAllPendingEventsSuccessfully() {
        service.publishPendingEvents();

        var inOrder = inOrder(transaction, event1, event2, repository, publisher, orderMetrics);

        inOrder.verify(repository).findAllUnpublished();

        inOrder.verify(orderMetrics).pendingEvents(2);
        inOrder.verify(transaction).execute(any());
        inOrder.verify(event1).markAsPublished();
        inOrder.verify(repository).save(event1);
        inOrder.verify(publisher).publish(event1);

        inOrder.verify(transaction).execute(any());
        inOrder.verify(event2).markAsPublished();
        inOrder.verify(repository).save(event2);
        inOrder.verify(publisher).publish(event2);

        verify(transaction, times(2)).execute(any());
        verifyNoMoreInteractions(repository, publisher);
    }

    @Test
    void continuesWhenOneEventFailsToPublish() {
        doThrow(new RuntimeException("Publishing failed")).when(publisher).publish(event1);

        service.publishPendingEvents();

        var inOrder = inOrder(transaction, event1, event2, repository, publisher, orderMetrics);

        inOrder.verify(repository).findAllUnpublished();

        inOrder.verify(orderMetrics).pendingEvents(2);
        inOrder.verify(transaction).execute(any());
        inOrder.verify(event1).markAsPublished();
        inOrder.verify(repository).save(event1);
        inOrder.verify(publisher).publish(event1);

        inOrder.verify(transaction).execute(any());
        inOrder.verify(event2).markAsPublished();
        inOrder.verify(repository).save(event2);
        inOrder.verify(publisher).publish(event2);

        verify(transaction, times(2)).execute(any());
        verifyNoMoreInteractions(repository, publisher);
    }

    @Test
    void continuesWhenMarkAsPublishedThrowsException() {
        doThrow(new RuntimeException("Mark as published failed")).when(event1).markAsPublished();

        service.publishPendingEvents();

        var inOrder = inOrder(transaction, event1, event2, repository, publisher, orderMetrics);

        inOrder.verify(repository).findAllUnpublished();

        inOrder.verify(orderMetrics).pendingEvents(2);
        inOrder.verify(transaction).execute(any());
        inOrder.verify(event1).markAsPublished();
        inOrder.verify(repository, never()).save(event1);
        inOrder.verify(publisher, never()).publish(event1);

        inOrder.verify(transaction).execute(any());
        inOrder.verify(event2).markAsPublished();
        inOrder.verify(repository).save(event2);
        inOrder.verify(publisher).publish(event2);

        verify(transaction, times(2)).execute(any());
        verifyNoMoreInteractions(repository, publisher);
    }

    @Test
    void stopsPublishingEventThatFailsToSave() {
        doThrow(new RuntimeException("Save failed")).when(repository).save(event1);

        service.publishPendingEvents();

        var inOrder = inOrder(event1, event2, transaction, repository, publisher, orderMetrics);

        inOrder.verify(repository).findAllUnpublished();

        inOrder.verify(orderMetrics).pendingEvents(2);
        inOrder.verify(transaction).execute(any());
        inOrder.verify(event1).markAsPublished();
        inOrder.verify(repository).save(event1);
        inOrder.verify(publisher, never()).publish(event1);

        inOrder.verify(transaction).execute(any());
        inOrder.verify(event2).markAsPublished();
        inOrder.verify(repository).save(event2);
        inOrder.verify(publisher).publish(event2);

        verify(transaction, times(2)).execute(any());
        verifyNoMoreInteractions(repository, publisher);
    }

    @Test
    void updatesMetricOnlyWhenNoPendingEventsFound() {
        when(repository.findAllUnpublished()).thenReturn(List.of());

        service.publishPendingEvents();

        verify(orderMetrics).pendingEvents(0);
        verify(repository).findAllUnpublished();
        verify(transaction, never()).execute(any());
        verifyNoMoreInteractions(repository, publisher);
    }

    private static OrderEvent mockOrderEvent(Long id) {
        var event = mock(OrderEvent.class);
        when(event.id()).thenReturn(OrderEventId.of(id));
        when(event.orderId()).thenReturn(OrderId.of(5L));
        when(event.type()).thenReturn(OrderEventType.CREATED);
        return event;
    }

}
