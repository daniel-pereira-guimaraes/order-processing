package com.danielpgbrasil.orderprocessing.infrastructure.metrics;

import com.danielpgbrasil.orderprocessing.application.metrics.OrderMetrics;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class MicrometerOrderMetrics implements OrderMetrics {

    private static final int ONE_HOUR = 60 * 60 * 1000;

    private final Queue<Long> failedEventsTimestamps = new ConcurrentLinkedQueue<>();
    private final AtomicInteger failedEventsLastHourGauge = new AtomicInteger(0);

    private final AtomicInteger pendingEventsGauge = new AtomicInteger(0);

    public MicrometerOrderMetrics(MeterRegistry registry) {

        Gauge.builder("failed_events_last_hour", failedEventsLastHourGauge, AtomicInteger::get)
                .description("Número de eventos que falharam nos últimos 60 minutos")
                .register(registry);

        registry.gauge("pending_events", pendingEventsGauge);
    }

    @Override
    public void incrementFailedEvents() {
        long now = Instant.now().toEpochMilli();
        failedEventsTimestamps.add(now);
        updateFailedEventsGauge();
    }

    private void updateFailedEventsGauge() {
        long cutoff = Instant.now().toEpochMilli() - ONE_HOUR;
        failedEventsTimestamps.removeIf(aLong -> aLong < cutoff);
        failedEventsLastHourGauge.set(failedEventsTimestamps.size());
    }

    @Override
    public void pendingEvents(int count) {
        pendingEventsGauge.set(count);
    }

    @Scheduled(fixedRate = 5000)
    public void scheduledUpdateFailedEventsGauge() {
        updateFailedEventsGauge();
    }
}
