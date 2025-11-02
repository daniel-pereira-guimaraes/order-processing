package com.danielpgbrasil.orderprocessing.domain.shared;

public class AbstractNotFoundException extends IllegalStateException {

    protected AbstractNotFoundException(String message) {
        super(message);
    }

}
