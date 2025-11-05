package com.danielpgbrasil.orderprocessing.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMqConfig.class);

    private static final String ERROR_SUFFIX = ".error";
    private static final String EXCHANGE_SUFFIX = "-exchange";
    private static final String QUEUE_SUFFIX = "-queue";
    private static final String QUEUE_SUFFIX_REGEX = QUEUE_SUFFIX + "$";

    public static final String ORDER_ROUTING_KEY = "order-events";
    public static final String ORDER_EVENTS_QUEUE = ORDER_ROUTING_KEY + QUEUE_SUFFIX;
    public static final String ORDER_EVENTS_EXCHANGE = ORDER_ROUTING_KEY + EXCHANGE_SUFFIX;

    private final FailedMessageEnricher failedMessageEnricher;

    public RabbitMqConfig(FailedMessageEnricher failedMessageEnricher) {
        this.failedMessageEnricher = failedMessageEnricher;
    }

    @Bean
    public MessageConverter jsonMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    public DirectExchange orderExchange() {
        return new DirectExchange(ORDER_EVENTS_EXCHANGE, true, false);
    }

    @Bean
    public Queue orderQueue() {
        return QueueBuilder.durable(ORDER_EVENTS_QUEUE).build();
    }

    @Bean
    public Binding orderBinding(Queue orderQueue, DirectExchange orderExchange) {
        return BindingBuilder.bind(orderQueue).to(orderExchange).with(ORDER_ROUTING_KEY);
    }

    @Bean
    public Queue orderErrorQueue() {
        return QueueBuilder.durable(ORDER_EVENTS_QUEUE + ERROR_SUFFIX).build();
    }

    @Bean
    public Binding orderErrorBinding(Queue orderErrorQueue, DirectExchange orderExchange) {
        return BindingBuilder.bind(orderErrorQueue).to(orderExchange).with(ORDER_EVENTS_QUEUE + ERROR_SUFFIX);
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            SimpleRabbitListenerContainerFactoryConfigurer configurer) {
        var factory = new SimpleRabbitListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        return factory;
    }

    @Bean
    public MessageRecoverer dynamicMessageRecoverer(RabbitTemplate rabbitTemplate) {
        return (message, cause) -> {
            var originalQueue = message.getMessageProperties().getConsumerQueue();
            var errorRoutingKey = originalQueue + ERROR_SUFFIX;
            var exchange = originalQueue.replaceFirst(QUEUE_SUFFIX_REGEX, EXCHANGE_SUFFIX);
            var messageCopy = failedMessageEnricher.enrich(message, cause);
            rabbitTemplate.convertAndSend(exchange, errorRoutingKey, messageCopy);
            LOGGER.error("Mensagem movida para fila de erro: {}", errorRoutingKey);
        };
    }

}
