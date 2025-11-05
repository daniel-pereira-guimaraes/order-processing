package com.danielpgbrasil.orderprocessing.infrastructure.shared;

import org.springframework.stereotype.Component;

import java.io.PrintWriter;
import java.io.StringWriter;

@Component
public class ExceptionDetailsExtractor {

    public String rootCauseMessage(Throwable throwable) {
        var root = findRootCause(throwable);
        var className = root.getClass().getName();
        var message = root.getMessage();
        return message == null ? className : className + ": " + message;
    }

    public String stackTraceToString(Throwable throwable) {
        var sw = new StringWriter();
        var pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        return sw.toString();
    }

    private Throwable findRootCause(Throwable cause) {
        while (cause.getCause() != null && cause.getCause() != cause) {
            cause = cause.getCause();
        }
        return cause;
    }
}
