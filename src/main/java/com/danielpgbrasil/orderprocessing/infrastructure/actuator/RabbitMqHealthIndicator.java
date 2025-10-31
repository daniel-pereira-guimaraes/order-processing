package com.danielpgbrasil.orderprocessing.infrastructure.actuator;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class RabbitMqHealthIndicator implements HealthIndicator {

    private static final Random RANDOM = new Random();

    @Override
    public Health health() {
        boolean rabbitUp = checkRabbitConnection();
        if (rabbitUp) {
            return Health.up().withDetail("RabbitMQ", "Available").build();
        } else {
            return Health.down().withDetail("RabbitMQ", "Not available").build();
        }
    }

    private boolean checkRabbitConnection() {
        return RANDOM.nextBoolean(); //TODO: Implementar
    }
}
