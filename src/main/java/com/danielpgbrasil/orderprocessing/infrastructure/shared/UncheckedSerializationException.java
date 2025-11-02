package com.danielpgbrasil.orderprocessing.infrastructure.shared;

public class UncheckedSerializationException extends RuntimeException {
    public UncheckedSerializationException(Throwable cause) {
        super(cause);
    }
}
