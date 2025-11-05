package com.danielpgbrasil.orderprocessing.infrastructure.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.core.Message;
import org.springframework.stereotype.Component;

@Component
public class DynamicMessageRecovererFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(DynamicMessageRecovererFactory.class);

    private final RabbitTemplate rabbitTemplate;
    private final FailedMessageEnricher failedMessageEnricher;

    public DynamicMessageRecovererFactory(RabbitTemplate rabbitTemplate, FailedMessageEnricher failedMessageEnricher) {
        this.rabbitTemplate = rabbitTemplate;
        this.failedMessageEnricher = failedMessageEnricher;
    }

    public MessageRecoverer create(String errorSuffix, String queueSuffixRegex, String exchangeSuffix) {
        return (Message message, Throwable cause) -> {
            var originalQueue = message.getMessageProperties().getConsumerQueue();
            var errorRoutingKey = originalQueue + errorSuffix;
            var exchange = originalQueue.replaceFirst(queueSuffixRegex, exchangeSuffix);
            var messageCopy = failedMessageEnricher.enrich(message, cause);
            rabbitTemplate.convertAndSend(exchange, errorRoutingKey, messageCopy);
            LOGGER.error("Mensagem movida para fila de erro: {}", errorRoutingKey);
        };
    }
}
