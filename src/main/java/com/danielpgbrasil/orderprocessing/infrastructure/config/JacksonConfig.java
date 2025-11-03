package com.danielpgbrasil.orderprocessing.infrastructure.config;

import com.danielpgbrasil.orderprocessing.domain.order.OrderDetails;
import com.danielpgbrasil.orderprocessing.domain.order.event.OrderEvent;
import com.danielpgbrasil.orderprocessing.infrastructure.shared.OrderEventDeserializer;
import com.danielpgbrasil.orderprocessing.infrastructure.shared.OrderEventSerializer;
import com.danielpgbrasil.orderprocessing.infrastructure.shared.OrderDetailsSerializer;
import com.danielpgbrasil.orderprocessing.infrastructure.shared.OrderDetailsDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        var mapper = new ObjectMapper();
        var module = new SimpleModule();
        module.addSerializer(OrderEvent.class, new OrderEventSerializer());
        module.addDeserializer(OrderEvent.class, new OrderEventDeserializer());
        module.addSerializer(OrderDetails.class, new OrderDetailsSerializer());
        module.addDeserializer(OrderDetails.class, new OrderDetailsDeserializer());
        mapper.registerModule(module);
        return mapper;
    }
}
