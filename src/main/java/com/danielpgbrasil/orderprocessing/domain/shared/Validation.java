package com.danielpgbrasil.orderprocessing.domain.shared;

import java.util.Collection;

public class Validation {

    private Validation() {
    }

    public static <T> T required(T value, String message) {
        if (value == null) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }

    public static String requireNonBlank(String value, String message) {
        var trimmed = required(value, message).trim();
        if (trimmed.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return trimmed;
    }

    public static <T extends Collection<?>> T requireNonEmpty(T value, String message) {
        var nonNull = required(value, message);
        if (nonNull.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
        return nonNull;
    }

}
