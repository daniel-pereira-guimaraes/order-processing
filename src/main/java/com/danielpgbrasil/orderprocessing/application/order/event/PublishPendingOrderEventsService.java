package com.danielpgbrasil.orderprocessing.application.order.event;

import com.danielpgbrasil.orderprocessing.application.metrics.OrderMetrics;
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
    private final OrderMetrics orderMetrics;

    public PublishPendingOrderEventsService(AppTransaction transaction,
                                            OrderEventRepository repository,
                                            OrderEventPublisher publisher,
                                            OrderMetrics orderMetrics) {
        this.transaction = transaction;
        this.repository = repository;
        this.publisher = publisher;
        this.orderMetrics = orderMetrics;
    }

    public void publishPendingEvents() {
        LOGGER.debug("Verificando eventos não publicados");
        var unpublishedEvents = repository.findAllUnpublished();
        this.orderMetrics.pendingEvents(unpublishedEvents.size());
        if (!unpublishedEvents.isEmpty()) {
            LOGGER.info("Publicando eventos: count={}", unpublishedEvents.size());
            unpublishedEvents.forEach(this::tryPublishEvent);
        }
    }

    private void tryPublishEvent(OrderEvent event) {
        logEvent("Publicando evento", event);
        try {
            transaction.execute(() -> publishEvent(event));
            logEvent("Evento publicado com sucesso", event);
        } catch (RuntimeException e) {
            logFailure(event, e);
        }
    }

    private static void logEvent(String message, OrderEvent event) {
        LOGGER.info("{}: id={}, orderId={}, type={}",
                message, event.id().value(), event.orderId().value(), event.type());
    }

    private static void logFailure(OrderEvent event, RuntimeException e) {
        LOGGER.error("Falha ao publicar o evento: id={}, orderId={}, type={}, error={}",
                event.id().value(), event.orderId().value(), event.type(), e.getMessage(), e);
    }

    private void publishEvent(OrderEvent event) {
        event.markAsPublished();
        repository.save(event);
        publisher.publish(event);
    }

}
