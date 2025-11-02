package com.danielpgbrasil.orderprocessing.infrastructure.shared;

import com.danielpgbrasil.orderprocessing.domain.order.OrderDetails;
import com.danielpgbrasil.orderprocessing.domain.order.OrderItem;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class OrderDetailsSerializer extends JsonSerializer<OrderDetails> {

    @Override
    public void serialize(OrderDetails value,
                          JsonGenerator gen,
                          SerializerProvider serializers)
            throws IOException {

        gen.writeStartObject();
        gen.writeStringField("customerName", value.customerName());
        gen.writeStringField("customerAddress", value.customerAddress());

        gen.writeArrayFieldStart("items");
        for (OrderItem item : value.items()) {
            gen.writeStartObject();
            gen.writeNumberField("productId", item.productId());
            gen.writeNumberField("quantity", item.quantity());
            gen.writeNumberField("price", item.price().doubleValue());
            gen.writeEndObject();
        }
        gen.writeEndArray();

        gen.writeEndObject();
    }
}
