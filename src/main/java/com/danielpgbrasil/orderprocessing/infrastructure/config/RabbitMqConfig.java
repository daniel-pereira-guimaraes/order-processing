package com.danielpgbrasil.orderprocessing.infrastructure.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    public static final String ORDER_EVENTS_ROUTING_KEY = "order-events";

    @Bean
    Queue orderQueue() {
        return QueueBuilder.durable("order-events-queue").build();
    }

    @Bean
    DirectExchange orderExchange() {
        return new DirectExchange("order-events-exchange");
    }

    @Bean
    Binding orderBinding(Queue orderQueue, DirectExchange orderExchange) {
        return BindingBuilder.bind(orderQueue).to(orderExchange).with(ORDER_EVENTS_ROUTING_KEY);
    }
}
