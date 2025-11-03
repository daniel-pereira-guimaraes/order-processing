package com.danielpgbrasil.orderprocessing.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;

@Configuration
public class RabbitMqConfig {

    private static final int MAX_ATTEMPTS = 10;
    private static final long RETRY_INTERVAL_MS = 5000;
    private static final String ERROR_SUFFIX = ".error";
    private static final String EXCHANGE_SUFFIX = "-exchange";
    private static final String QUEUE_SUFFIX = "-queue";
    private static final String QUEUE_SUFFIX_REGEX = QUEUE_SUFFIX + '$';

    public static final String ORDER_EVENTS = "order-events";
    public static final String ORDER_QUEUE = ORDER_EVENTS + QUEUE_SUFFIX;
    public static final String ORDER_EXCHANGE = ORDER_EVENTS + EXCHANGE_SUFFIX;
    public static final String ORDER_ROUTING_KEY = ORDER_EVENTS;

    @Bean
    public MessageConverter jsonMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    public RetryTemplate retryTemplate() {
        var retryTemplate = new RetryTemplate();
        retryTemplate.setBackOffPolicy(buildBackOffPolicy());
        retryTemplate.setRetryPolicy(buildRetryPolice());
        return retryTemplate;
    }

    private static FixedBackOffPolicy buildBackOffPolicy() {
        var backOff = new FixedBackOffPolicy();
        backOff.setBackOffPeriod(RETRY_INTERVAL_MS);
        return backOff;
    }

    private static SimpleRetryPolicy buildRetryPolice() {
        var retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(MAX_ATTEMPTS);
        return retryPolicy;
    }

    private Queue buildQueue(String queueName) {
        return QueueBuilder.durable(queueName).build();
    }

    private DirectExchange buildExchange(String exchangeName) {
        return new DirectExchange(exchangeName);
    }

    private Binding buildBinding(Queue queue, DirectExchange exchange, String routingKey) {
        return BindingBuilder.bind(queue).to(exchange).with(routingKey);
    }

    private Queue buildErrorQueue(String queueName) {
        return QueueBuilder.durable(queueName + ERROR_SUFFIX).build();
    }

    private Binding buildErrorBinding(Queue errorQueue, DirectExchange exchange, String routingKey) {
        return BindingBuilder.bind(errorQueue).to(exchange).with(routingKey);
    }

    @Bean
    public Queue orderQueue() { return buildQueue(ORDER_QUEUE); }

    @Bean
    public DirectExchange orderExchange() { return buildExchange(ORDER_EXCHANGE); }

    @Bean
    public Binding orderBinding(Queue orderQueue, DirectExchange orderExchange) {
        return buildBinding(orderQueue, orderExchange, ORDER_ROUTING_KEY);
    }

    @Bean
    public Queue orderErrorQueue() { return buildErrorQueue(ORDER_QUEUE); }

    @Bean
    public Binding orderErrorBinding(Queue orderErrorQueue, DirectExchange orderExchange) {
        return buildErrorBinding(orderErrorQueue, orderExchange, ORDER_ROUTING_KEY + ERROR_SUFFIX);
    }

    @Bean
    public MessageRecoverer dynamicMessageRecoverer(RabbitTemplate rabbitTemplate) {
        return (message, cause) -> {
            var originalQueue = message.getMessageProperties().getConsumerQueue();
            var errorQueue = originalQueue + ERROR_SUFFIX;
            var exchange = originalQueue.replaceFirst(QUEUE_SUFFIX_REGEX, EXCHANGE_SUFFIX);
            var messageCopy = buildMessage(message, cause);
            rabbitTemplate.convertAndSend(exchange, errorQueue, messageCopy);
        };
    }

    private static Message buildMessage(Message message, Throwable cause) {
        return MessageBuilder
                .fromMessage(message)
                .setHeader("x-exception-class", cause.getClass().getName())
                .setHeader("x-exception-message", cause.getMessage())
                .setHeader("x-exception-stacktrace", stackTraceToString(cause))
                .setHeader("x-failed-at", Instant.now().toString())
                .build();
    }

    private static String stackTraceToString(Throwable cause) {
        var sw = new StringWriter();
        var pw = new PrintWriter(sw);
        cause.printStackTrace(pw);
        return sw.toString();
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            SimpleRabbitListenerContainerFactoryConfigurer configurer,
            RetryTemplate retryTemplate,
            MessageRecoverer dynamicMessageRecoverer) {

        var factory = new SimpleRabbitListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        factory.setDefaultRequeueRejected(false);
        factory.setAdviceChain(buildRetryInterceptor(retryTemplate, dynamicMessageRecoverer));

        return factory;
    }

    private static RetryOperationsInterceptor buildRetryInterceptor(
            RetryTemplate retryTemplate, MessageRecoverer dynamicMessageRecoverer) {
        return RetryInterceptorBuilder
                .stateless()
                .retryOperations(retryTemplate)
                .recoverer(dynamicMessageRecoverer)
                .build();
    }
}
