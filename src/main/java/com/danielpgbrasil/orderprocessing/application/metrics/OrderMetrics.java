package com.danielpgbrasil.orderprocessing.application.metrics;

public interface OrderMetrics {
    void incrementFailedEvents();
    void pendingEvents(int count);
}
