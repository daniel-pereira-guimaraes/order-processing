package com.danielpgbrasil.orderprocessing.it.infrastructure.shared;

import com.danielpgbrasil.orderprocessing.domain.order.event.OrderEvent;
import com.danielpgbrasil.orderprocessing.domain.order.event.OrderEventId;
import com.danielpgbrasil.orderprocessing.domain.order.event.OrderEventType;
import com.danielpgbrasil.orderprocessing.fixture.OrderEventFixture;
import com.danielpgbrasil.orderprocessing.infrastructure.shared.OrderEventDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OrderEventDeserializerTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void beforeEach() {
        var module = new SimpleModule();
        module.addDeserializer(OrderEvent.class, new OrderEventDeserializer());

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(module);
    }

    @ParameterizedTest
    @EnumSource(OrderEventType.class)
    void deserializesCorrectlyWhenTypeVaries(OrderEventType type) throws Exception {
        var inputEvent = OrderEventFixture.builder()
                .withType(type)
                .build();

        var json = buildJson(inputEvent);

        var outputEvent = objectMapper.readValue(json, OrderEvent.class);

        assertEquals(inputEvent, outputEvent);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {123L, 999L})
    void deserializesCorrectlyWhenIdVaries(Long idValue) throws Exception {
        var eventId = idValue != null ? OrderEventId.of(idValue) : null;
        var inputEvent = OrderEventFixture.builder()
                .withId(eventId)
                .build();

        var json = buildJson(inputEvent);

        var outputEvent = objectMapper.readValue(json, OrderEvent.class);

        assertEquals(inputEvent, outputEvent);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void deserializesCorrectlyWhenPublishedVaries(boolean published) throws Exception {
        var inputEvent = OrderEventFixture.builder()
                .withPublished(published)
                .build();

        var json = buildJson(inputEvent);

        var outputEvent = objectMapper.readValue(json, OrderEvent.class);

        assertEquals(inputEvent, outputEvent);
    }

    private String buildJson(OrderEvent event) {
        return String.format(
                "{\"id\":%s,\"orderId\":%d,\"type\":\"%s\",\"createdAt\":%d,\"published\":%b}",
                event.id() != null ? event.id().value() : "null",
                event.orderId().value(),
                event.type().name(),
                event.createdAt().value(),
                event.isPublished()
        );
    }
}
