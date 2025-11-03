package com.danielpgbrasil.orderprocessing.infrastructure.messaging;

import com.danielpgbrasil.orderprocessing.application.order.event.PublishPendingOrderEventsService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PublishPendingOrderEventsScheduler {

    private final PublishPendingOrderEventsService publishService;

    public PublishPendingOrderEventsScheduler(PublishPendingOrderEventsService publishService) {
        this.publishService = publishService;
    }

    @Scheduled(fixedDelay = 10000L)
    public void run() {
        publishService.publishPendingEvents();
    }
}
