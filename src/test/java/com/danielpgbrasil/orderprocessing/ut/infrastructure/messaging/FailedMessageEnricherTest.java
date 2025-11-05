package com.danielpgbrasil.orderprocessing.ut.infrastructure.messaging;

import com.danielpgbrasil.orderprocessing.infrastructure.messaging.FailedMessageEnricher;
import com.danielpgbrasil.orderprocessing.infrastructure.shared.ExceptionDetailsExtractor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class FailedMessageEnricherTest {

    private ExceptionDetailsExtractor exceptionDetailsExtractor;
    private FailedMessageEnricher enricher;

    @BeforeEach
    void setUp() {
        exceptionDetailsExtractor = mock(ExceptionDetailsExtractor.class);
        enricher = new FailedMessageEnricher(exceptionDetailsExtractor);
    }

    @Test
    void enrichAddsErrorHeadersWhenCauseIsProvided() {
        var originalMessage = MessageBuilder.withBody("test".getBytes()).build();
        var throwable = new RuntimeException("something went wrong");

        when(exceptionDetailsExtractor.rootCauseMessage(throwable)).thenReturn("Root cause message");
        when(exceptionDetailsExtractor.stackTraceToString(throwable)).thenReturn("stack trace");

        Message enrichedMessage = enricher.enrich(originalMessage, throwable);

        assertThat(enrichedMessage, notNullValue());
        assertThat(enrichedMessage.getBody(), is(originalMessage.getBody()));
        assertThat(enrichedMessage.getMessageProperties().getHeaders().get("x-error-root"), is("Root cause message"));
        assertThat(enrichedMessage.getMessageProperties().getHeaders().get("x-error-trace"), is("stack trace"));
        assertThat(enrichedMessage.getMessageProperties().getHeaders().get("x-error-time"), notNullValue());

        verify(exceptionDetailsExtractor).rootCauseMessage(throwable);
        verify(exceptionDetailsExtractor).stackTraceToString(throwable);
    }

    @Test
    void propagatesExceptionWhenRootCauseMessageThrows() {
        var originalMessage = MessageBuilder.withBody("test".getBytes()).build();
        var throwable = new RuntimeException();
        when(exceptionDetailsExtractor.rootCauseMessage(throwable)).thenThrow(new RuntimeException("fail"));

        var exception = assertThrows(RuntimeException.class,
                () -> enricher.enrich(originalMessage, throwable)
        );

        assertThat(exception.getMessage(), is("fail"));
    }

    @Test
    void propagatesExceptionWhenStackTraceToStringThrows() {
        var originalMessage = MessageBuilder.withBody("test".getBytes()).build();
        var throwable = new RuntimeException();

        when(exceptionDetailsExtractor.rootCauseMessage(throwable)).thenReturn("root");
        when(exceptionDetailsExtractor.stackTraceToString(throwable)).thenThrow(new RuntimeException("fail trace"));

        var exception = assertThrows(RuntimeException.class,
                () -> enricher.enrich(originalMessage, throwable)
        );

        assertThat(exception.getMessage(), is("fail trace"));
    }
}
