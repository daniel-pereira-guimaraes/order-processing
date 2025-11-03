package com.danielpgbrasil.orderprocessing.infrastructure.messaging;

import com.danielpgbrasil.orderprocessing.application.order.MarkOrderDeliveredService;
import com.danielpgbrasil.orderprocessing.application.order.StartOrderPickingService;
import com.danielpgbrasil.orderprocessing.application.order.StartOrderTransitService;
import com.danielpgbrasil.orderprocessing.domain.order.event.OrderEvent;
import com.danielpgbrasil.orderprocessing.infrastructure.config.RabbitMqConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class OrderEventConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderEventConsumer.class);

    private final StartOrderPickingService pickingService;
    private final StartOrderTransitService transitService;
    private final MarkOrderDeliveredService deliveryService;

    public OrderEventConsumer(
            StartOrderPickingService pickingService,
            StartOrderTransitService transitService,
            MarkOrderDeliveredService deliveryService) {
        this.pickingService = pickingService;
        this.transitService = transitService;
        this.deliveryService = deliveryService;
    }

    @RabbitListener(queues = RabbitMqConfig.ORDER_QUEUE)
    public void handleOrderEvent(OrderEvent event) {
        try {
            LOGGER.info("Consumindo evento: id={}, orderId={}, type={}",
                    event.id().value(), event.orderId().value(), event.type());
            tryProcessEvent(event);
        } catch (RuntimeException e) {
            LOGGER.error("Erro consumindo evento: id={}, orderId={}, type={}",
                    event.id().value(), event.orderId().value(), event.type());
            throw e;
        }
    }

    private void tryProcessEvent(OrderEvent event) {
        switch (event.type()) {
            case CREATED -> pickingService.startPicking(event.orderId());
            case PICKING_STARTED -> transitService.startTransit(event.orderId());
            case TRANSIT_STARTED -> deliveryService.markDelivered(event.orderId());
            default -> LOGGER.info("Evento ignorado: orderId={}, type={}", event.orderId().value(), event.type());
        }
    }
}
