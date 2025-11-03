package com.danielpgbrasil.orderprocessing.infrastructure.shared;

import com.danielpgbrasil.orderprocessing.domain.order.OrderId;
import com.danielpgbrasil.orderprocessing.domain.order.event.OrderEvent;
import com.danielpgbrasil.orderprocessing.domain.order.event.OrderEventType;
import com.danielpgbrasil.orderprocessing.domain.order.event.OrderEventId;
import com.danielpgbrasil.orderprocessing.domain.shared.TimeMillis;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

public class OrderEventDeserializer extends JsonDeserializer<OrderEvent> {

    @Override
    public OrderEvent deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        ObjectNode node = parser.getCodec().readTree(parser);
        var builder = OrderEvent.builder();

        builder.withId(asLong(node.get("id"), OrderEventId::of));
        builder.withOrderId(asLong(node.get("orderId"), OrderId::of));
        builder.withType(asText(node.get("type"), OrderEventType::valueOf));
        builder.withCreatedAt(asLong(node.get("createdAt"), TimeMillis::of));
        builder.withPublished(asBoolean(node.get("published")));

        return builder.build();
    }

    private <T> T asLong(JsonNode node, java.util.function.LongFunction<T> mapper) {
        return (node != null && !node.isNull()) ? mapper.apply(node.asLong()) : null;
    }

    private <T> T asText(JsonNode node, java.util.function.Function<String, T> mapper) {
        return (node != null && !node.isNull()) ? mapper.apply(node.asText()) : null;
    }

    private Boolean asBoolean(JsonNode node) {
        return (node != null && !node.isNull()) ? node.asBoolean() : null;
    }
}
