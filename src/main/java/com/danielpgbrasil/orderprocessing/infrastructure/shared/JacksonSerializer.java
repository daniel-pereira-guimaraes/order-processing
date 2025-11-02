package com.danielpgbrasil.orderprocessing.infrastructure.shared;

import com.danielpgbrasil.orderprocessing.domain.order.OrderDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.stereotype.Component;

@Component
public class JacksonSerializer implements Serializer {

    private final ObjectMapper objectMapper;

    public JacksonSerializer() {
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(buildSimpleModule());
    }

    private static SimpleModule buildSimpleModule() {
        var module = new SimpleModule();
        module.addSerializer(OrderDetails.class, new OrderDetailsSerializer());
        module.addDeserializer(OrderDetails.class, new OrderDetailsDeserializer());
        return module;
    }

    @Override
    public String serialize(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            throw new UncheckedSerializationException(e);
        }
    }

    @Override
    public <T> T deserialize(String value, Class<T> clazz) {
        try {
            return objectMapper.readValue(value, clazz);
        } catch (Exception e) {
            throw new UncheckedSerializationException(e);
        }
    }
}
