package com.danielpgbrasil.orderprocessing.infrastructure.messaging;

import com.danielpgbrasil.orderprocessing.application.order.event.OrderEventPublisher;
import com.danielpgbrasil.orderprocessing.domain.order.event.OrderEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import static com.danielpgbrasil.orderprocessing.infrastructure.messaging.RabbitMqConfig.*;

@Component
public class RabbitMqOrderEventPublisher implements OrderEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public RabbitMqOrderEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publish(OrderEvent event) {
        rabbitTemplate.convertAndSend(ORDER_EVENTS_EXCHANGE, ORDER_ROUTING_KEY, event);
    }
}
