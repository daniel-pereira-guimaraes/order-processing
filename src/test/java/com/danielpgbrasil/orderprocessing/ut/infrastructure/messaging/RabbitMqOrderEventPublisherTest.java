package com.danielpgbrasil.orderprocessing.ut.infrastructure.messaging;

import com.danielpgbrasil.orderprocessing.domain.order.event.OrderEvent;
import com.danielpgbrasil.orderprocessing.infrastructure.messaging.RabbitMqOrderEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static com.danielpgbrasil.orderprocessing.infrastructure.messaging.RabbitMqConfig.ORDER_EVENTS_EXCHANGE;
import static com.danielpgbrasil.orderprocessing.infrastructure.messaging.RabbitMqConfig.ORDER_ROUTING_KEY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class RabbitMqOrderEventPublisherTest {

    private RabbitTemplate rabbitTemplate;
    private RabbitMqOrderEventPublisher publisher;

    @BeforeEach
    void setUp() {
        rabbitTemplate = mock(RabbitTemplate.class);
        publisher = new RabbitMqOrderEventPublisher(rabbitTemplate);
    }

    @Test
    void publishSendsMessageAndSetsPersistentMode() {
        var event = mock(OrderEvent.class);
        var postProcessorCaptor = ArgumentCaptor.forClass(MessagePostProcessor.class);

        publisher.publish(event);

        verify(rabbitTemplate).convertAndSend(
                eq(ORDER_EVENTS_EXCHANGE),
                eq(ORDER_ROUTING_KEY),
                eq(event),
                postProcessorCaptor.capture()
        );

        var capturedProcessor = postProcessorCaptor.getValue();
        var props = new MessageProperties();
        var originalMsg = new Message(new byte[0], props);
        var processedMsg = capturedProcessor.postProcessMessage(originalMsg);
        assertThat(processedMsg.getMessageProperties().getDeliveryMode(), is(MessageDeliveryMode.PERSISTENT));
        verifyNoMoreInteractions(rabbitTemplate);
    }

    @Test
    void propagatesExceptionWhenRabbitTemplateFails() {
        var event = mock(OrderEvent.class);

        doThrow(new RuntimeException("RabbitMQ down"))
                .when(rabbitTemplate)
                .convertAndSend(any(String.class), any(String.class), any(), any(MessagePostProcessor.class));

        assertThrows(RuntimeException.class, () -> publisher.publish(event));
    }
}