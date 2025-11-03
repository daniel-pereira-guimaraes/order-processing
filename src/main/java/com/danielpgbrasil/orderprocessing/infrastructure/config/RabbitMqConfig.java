package com.danielpgbrasil.orderprocessing.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    public static final String ORDER_EVENTS_ROUTING_KEY = "order-events";

    @Bean
    public MessageConverter jsonMessageConverter(ObjectMapper objectMapper) {
        var converter = new Jackson2JsonMessageConverter(objectMapper);
        converter.setCreateMessageIds(true);
        return converter;
    }

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
