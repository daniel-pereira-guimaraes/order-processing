package com.danielpgbrasil.orderprocessing.infrastructure.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    @Value("${app.rabbitmq.order.queue}")
    private String orderQueueName;

    @Value("${app.rabbitmq.order.exchange}")
    private String orderExchangeName;

    @Value("${app.rabbitmq.order.routing-key}")
    private String orderRoutingKey;

    @Bean
    Queue orderQueue() {
        return QueueBuilder.durable(orderQueueName).build();
    }

    @Bean
    DirectExchange orderExchange() {
        return new DirectExchange(orderExchangeName);
    }

    @Bean
    Binding orderBinding(Queue orderQueue, DirectExchange orderExchange) {
        return BindingBuilder.bind(orderQueue)
                .to(orderExchange)
                .with(orderRoutingKey);
    }

}
