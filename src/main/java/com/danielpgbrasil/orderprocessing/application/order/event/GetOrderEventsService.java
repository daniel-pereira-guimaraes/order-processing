package com.danielpgbrasil.orderprocessing.application.order.event;

import com.danielpgbrasil.orderprocessing.domain.order.OrderId;
import com.danielpgbrasil.orderprocessing.domain.order.OrderNotFoundException;
import com.danielpgbrasil.orderprocessing.domain.order.OrderRepository;
import com.danielpgbrasil.orderprocessing.domain.order.event.OrderEvent;
import com.danielpgbrasil.orderprocessing.domain.order.event.OrderEventRepository;

import java.util.List;

public class GetOrderEventsService {

    private final OrderRepository orderRepository;
    private final OrderEventRepository orderEventRepository;

    public GetOrderEventsService(OrderRepository orderRepository,
                                 OrderEventRepository orderEventRepository) {
        this.orderRepository = orderRepository;
        this.orderEventRepository = orderEventRepository;
    }

    public List<OrderEvent> getEvents(OrderId orderId) {
        if (!orderRepository.exists(orderId)) {
            throw new OrderNotFoundException(orderId);
        }
        return orderEventRepository.findByOrderId(orderId);
    }
}
