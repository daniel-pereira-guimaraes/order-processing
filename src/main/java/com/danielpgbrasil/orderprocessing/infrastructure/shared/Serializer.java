package com.danielpgbrasil.orderprocessing.infrastructure.shared;

public interface Serializer {
    String serialize(Object object);
    <T> T deserialize(String value, Class<T> clazz);
}