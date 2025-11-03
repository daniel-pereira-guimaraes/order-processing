package com.danielpgbrasil.orderprocessing.infrastructure.messaging;

import com.danielpgbrasil.orderprocessing.application.order.event.PublishPendingOrderEventsService;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class AsyncPublishPendingOrderEventsService {

    private final PublishPendingOrderEventsService service;

    public AsyncPublishPendingOrderEventsService(@Lazy PublishPendingOrderEventsService service) {
        this.service = service;
    }

    @Async
    public void execute() {
        service.publishPendingEvents();
    }
}
