package com.danielpgbrasil.orderprocessing.ut.infrastructure.messaging;

import com.danielpgbrasil.orderprocessing.infrastructure.messaging.DynamicMessageRecovererFactory;
import com.danielpgbrasil.orderprocessing.infrastructure.messaging.FailedMessageEnricher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class DynamicMessageRecovererFactoryTest {

    private RabbitTemplate rabbitTemplate;
    private FailedMessageEnricher failedMessageEnricher;
    private DynamicMessageRecovererFactory factory;

    private static final String ERROR_SUFFIX = ".error";
    private static final String QUEUE_SUFFIX_REGEX = "-queue$";
    private static final String EXCHANGE_SUFFIX = "-exchange";

    @BeforeEach
    void beforeEach() {
        rabbitTemplate = mock(RabbitTemplate.class);
        failedMessageEnricher = mock(FailedMessageEnricher.class);
        factory = new DynamicMessageRecovererFactory(rabbitTemplate, failedMessageEnricher);
    }

    @Test
    void sendsMessageToErrorQueueSuccessfully() {
        var message = mock(Message.class);
        var props = mock(MessageProperties.class);
        var enrichedMessage = mock(Message.class);
        when(message.getMessageProperties()).thenReturn(props);
        when(props.getConsumerQueue()).thenReturn("order-events-queue");
        when(failedMessageEnricher.enrich(message, null)).thenReturn(enrichedMessage);

        var recoverer = factory.create(ERROR_SUFFIX, QUEUE_SUFFIX_REGEX, EXCHANGE_SUFFIX);
        recoverer.recover(message, null);

        verify(failedMessageEnricher).enrich(message, null);
        verify(rabbitTemplate).convertAndSend("order-events-exchange", "order-events-queue.error", enrichedMessage);
    }

    @Test
    void propagatesExceptionWhenRabbitTemplateThrows() {
        var message = mock(Message.class);
        var props = mock(MessageProperties.class);
        var enrichedMessage = mock(Message.class);
        when(message.getMessageProperties()).thenReturn(props);
        when(props.getConsumerQueue()).thenReturn("order-events-queue");
        when(failedMessageEnricher.enrich(message, null)).thenReturn(enrichedMessage);
        doThrow(new RuntimeException("Rabbit error")).when(rabbitTemplate)
                .convertAndSend(anyString(), anyString(), any(Message.class));

        var recoverer = factory.create(ERROR_SUFFIX, QUEUE_SUFFIX_REGEX, EXCHANGE_SUFFIX);
        assertThrows(RuntimeException.class, () -> recoverer.recover(message, null));

        verify(failedMessageEnricher).enrich(message, null);
        verify(rabbitTemplate).convertAndSend("order-events-exchange", "order-events-queue.error", enrichedMessage);
    }

    @Test
    void propagatesExceptionWhenEnricherThrows() {
        var message = mock(Message.class);
        var props = mock(MessageProperties.class);
        when(message.getMessageProperties()).thenReturn(props);
        when(props.getConsumerQueue()).thenReturn("order-events-queue");
        doThrow(new RuntimeException("Enricher error")).when(failedMessageEnricher).enrich(message, null);

        var recoverer = factory.create(ERROR_SUFFIX, QUEUE_SUFFIX_REGEX, EXCHANGE_SUFFIX);
        assertThrows(RuntimeException.class, () -> recoverer.recover(message, null));

        verify(failedMessageEnricher).enrich(message, null);
        verifyNoInteractions(rabbitTemplate);
    }
}
