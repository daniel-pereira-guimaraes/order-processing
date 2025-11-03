package com.danielpgbrasil.orderprocessing.infrastructure;

import com.danielpgbrasil.orderprocessing.application.order.event.CreateOrderEventService;
import com.danielpgbrasil.orderprocessing.domain.order.Order;
import com.danielpgbrasil.orderprocessing.domain.order.OrderListener;
import com.danielpgbrasil.orderprocessing.domain.order.event.OrderEventType;
import com.danielpgbrasil.orderprocessing.infrastructure.messaging.AsyncPublishPendingOrderEventsService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class OrderListenerImpl implements OrderListener {

    private final CreateOrderEventService createOrderEventService;
    private final AsyncPublishPendingOrderEventsService asyncPublishPendingOrderEventsService;

    public OrderListenerImpl(@Lazy CreateOrderEventService createOrderEventService, AsyncPublishPendingOrderEventsService asyncPublishPendingOrderEventsService) {
        this.createOrderEventService = createOrderEventService;
        this.asyncPublishPendingOrderEventsService = asyncPublishPendingOrderEventsService;
    }

    @Override
    public void statusChanged(Order order) {
        var eventType = OrderEventType.fromStatus(order.status());
        createOrderEventService.createEvent(order, eventType);
        asyncPublishPendingOrderEventsService.execute();
    }

}
