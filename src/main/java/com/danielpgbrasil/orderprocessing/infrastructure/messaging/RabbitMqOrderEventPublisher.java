package com.danielpgbrasil.orderprocessing.infrastructure.messaging;

import com.danielpgbrasil.orderprocessing.application.order.event.OrderEventPublisher;
import com.danielpgbrasil.orderprocessing.domain.order.event.OrderEvent;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import static com.danielpgbrasil.orderprocessing.infrastructure.config.RabbitMqConfig.ORDER_EVENTS_ROUTING_KEY;

@Component
public class RabbitMqOrderEventPublisher implements OrderEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final DirectExchange orderExchange;

    public RabbitMqOrderEventPublisher(RabbitTemplate rabbitTemplate,
                                       DirectExchange orderExchange) {
        this.rabbitTemplate = rabbitTemplate;
        this.orderExchange = orderExchange;
    }

    @Override
    public void publish(OrderEvent event) {
        rabbitTemplate.convertAndSend(orderExchange.getName(), ORDER_EVENTS_ROUTING_KEY, event);
    }
}
