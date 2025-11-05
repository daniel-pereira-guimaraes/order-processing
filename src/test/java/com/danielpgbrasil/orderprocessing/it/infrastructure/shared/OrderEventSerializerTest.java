package com.danielpgbrasil.orderprocessing.it.infrastructure.shared;

import com.danielpgbrasil.orderprocessing.domain.order.event.OrderEvent;
import com.danielpgbrasil.orderprocessing.domain.order.event.OrderEventId;
import com.danielpgbrasil.orderprocessing.domain.order.event.OrderEventType;
import com.danielpgbrasil.orderprocessing.fixture.OrderEventFixture;
import com.danielpgbrasil.orderprocessing.infrastructure.shared.OrderEventSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OrderEventSerializerTest {

    private static final String JSON_PATTERN =
            "{\"id\":%d,\"orderId\":%d,\"type\":\"%s\",\"createdAt\":%d,\"published\":%b}";

    private ObjectMapper objectMapper;

    @BeforeEach
    void beforeEach() {
        var module = new SimpleModule();
        module.addSerializer(OrderEvent.class, new OrderEventSerializer());

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(module);
    }

    @ParameterizedTest
    @EnumSource(OrderEventType.class)
    void doesSerializeCorrectlyWhenTypeVaries(OrderEventType type) throws Exception {
        var event = OrderEventFixture.builder()
                .withType(type)
                .build();

        var json = serializeEvent(event);

        assertEquals(expectedJson(event), json);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {123L, 999L})
    void doesSerializeCorrectlyWhenIdVaries(Long idValue) throws Exception {
        var event = OrderEventFixture.builder()
                .withId(OrderEventId.ofNullable(idValue).orElse(null))
                .build();

        var json = serializeEvent(event);

        assertEquals(expectedJson(event), json);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void doesSerializeCorrectlyWhenPublishedVaries(boolean published) throws Exception {
        var event = OrderEventFixture.builder()
                .withPublished(published)
                .build();

        var json = serializeEvent(event);

        assertEquals(expectedJson(event), json);
    }

    private String serializeEvent(OrderEvent event) throws Exception {
        return objectMapper.writeValueAsString(event);
    }

    private String expectedJson(OrderEvent event) {
        return String.format(
                JSON_PATTERN,
                event.id() == null ? null : event.id().value(),
                event.orderId().value(),
                event.type().name(),
                event.createdAt().value(),
                event.isPublished()
        );
    }
}
