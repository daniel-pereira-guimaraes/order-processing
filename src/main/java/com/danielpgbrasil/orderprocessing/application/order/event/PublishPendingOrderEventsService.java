package com.danielpgbrasil.orderprocessing.application.order.event;

import com.danielpgbrasil.orderprocessing.application.shared.AppTransaction;
import com.danielpgbrasil.orderprocessing.domain.order.event.OrderEvent;
import com.danielpgbrasil.orderprocessing.domain.order.event.OrderEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PublishPendingOrderEventsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PublishPendingOrderEventsService.class);

    private final AppTransaction transaction;
    private final OrderEventRepository repository;
    private final OrderEventPublisher publisher;

    public PublishPendingOrderEventsService(AppTransaction transaction,
                                            OrderEventRepository repository,
                                            OrderEventPublisher publisher) {
        this.transaction = transaction;
        this.repository = repository;
        this.publisher = publisher;
    }

    public void publishPendingEvents() {
        repository.findAllUnpublished().forEach(this::tryPublishEvent);
    }

    private void tryPublishEvent(OrderEvent event) {
        try {
            transaction.execute(() -> publishEvent(event));
            logSuccess(event);
        } catch (RuntimeException e) {
            logFailure(event, e);
        }
    }

    private static void logSuccess(OrderEvent event) {
        LOGGER.info("Evento publicado com sucesso: orderId={}, eventId={}, type={}",
                event.orderId().value(), event.id().value(), event.type());
    }

    private static void logFailure(OrderEvent event, RuntimeException e) {
        LOGGER.error("Falha ao publicar o evento: orderId={}, eventId={}, type={}, error={}",
                event.orderId().value(), event.id().value(), event.type(), e.getMessage(), e);
    }

    private void publishEvent(OrderEvent event) {
        event.markAsPublished();
        repository.save(event);
        publisher.publish(event);
    }

}
