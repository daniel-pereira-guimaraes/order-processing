package com.danielpgbrasil.orderprocessing.infrastructure.messaging;

import com.danielpgbrasil.orderprocessing.infrastructure.shared.ExceptionDetailsExtractor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class FailedMessageEnricher {

    private final ExceptionDetailsExtractor exceptionDetailsExtractor;

    public FailedMessageEnricher(ExceptionDetailsExtractor exceptionDetailsExtractor) {
        this.exceptionDetailsExtractor = exceptionDetailsExtractor;
    }

    public Message enrich(Message message, Throwable cause) {
        return MessageBuilder
                .fromMessage(message)
                .setHeader("x-error-root", exceptionDetailsExtractor.rootCauseMessage(cause))
                .setHeader("x-error-trace", exceptionDetailsExtractor.stackTraceToString(cause))
                .setHeader("x-error-time", Instant.now().toString())
                .build();
    }
}
