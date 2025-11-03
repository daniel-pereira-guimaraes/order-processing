package com.danielpgbrasil.orderprocessing.it.infrastructure;

import com.danielpgbrasil.orderprocessing.application.order.event.OrderEventPublisher;
import com.danielpgbrasil.orderprocessing.domain.shared.AppClock;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import static org.mockito.Mockito.mock;

@TestConfiguration
class MocksConfig {

    @Bean
    public AppClock clock() {
        return mock(AppClock.class);
    }

    @Bean
    public OrderEventPublisher orderEventPublisher() {
        return mock(OrderEventPublisher.class);
    }
}
