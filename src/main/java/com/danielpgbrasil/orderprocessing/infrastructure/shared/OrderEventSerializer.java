package com.danielpgbrasil.orderprocessing.infrastructure.shared;

import com.danielpgbrasil.orderprocessing.domain.order.event.OrderEvent;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class OrderEventSerializer extends JsonSerializer<OrderEvent> {

    @Override
    public void serialize(OrderEvent event,
                          JsonGenerator gen,
                          SerializerProvider serializers) throws IOException {

        gen.writeStartObject();
        gen.writeNumberField("id", event.id().value());
        gen.writeNumberField("orderId", event.orderId().value());
        gen.writeStringField("type", event.type().name());
        gen.writeBooleanField("published", event.isPublished());
        gen.writeEndObject();
    }
}
