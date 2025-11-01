package com.danielpgbrasil.orderprocessing.application.order.event;

import com.danielpgbrasil.orderprocessing.domain.order.event.OrderEvent;

public interface OrderEventPublisher {
    void publish(OrderEvent event);
}
