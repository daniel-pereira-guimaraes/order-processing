package com.danielpgbrasil.orderprocessing.infrastructure;

import com.danielpgbrasil.orderprocessing.application.order.event.CreateOrderEventService;
import com.danielpgbrasil.orderprocessing.domain.order.Order;
import com.danielpgbrasil.orderprocessing.domain.order.OrderListener;
import com.danielpgbrasil.orderprocessing.domain.order.event.OrderEventType;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class OrderListenerImpl implements OrderListener {

    private final CreateOrderEventService createOrderEventService;

    public OrderListenerImpl(@Lazy CreateOrderEventService createOrderEventService) {
        this.createOrderEventService = createOrderEventService;
    }

    @Override
    public void statusChanged(Order order) {
        var eventType = OrderEventType.fromStatus(order.status());
        createOrderEventService.createEvent(order, eventType);
    }
}
