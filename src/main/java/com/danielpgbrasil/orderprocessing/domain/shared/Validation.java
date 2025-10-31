package com.danielpgbrasil.orderprocessing.domain.shared;

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

}
