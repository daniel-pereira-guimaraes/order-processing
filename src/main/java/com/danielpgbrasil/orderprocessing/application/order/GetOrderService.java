package com.danielpgbrasil.orderprocessing.application.order;

import com.danielpgbrasil.orderprocessing.domain.order.*;
import com.danielpgbrasil.orderprocessing.domain.order.event.OrderEvent;
import com.danielpgbrasil.orderprocessing.domain.order.event.OrderEventRepository;

import java.util.List;

public class GetOrderService {

    private final OrderRepository orderRepository;
    private final OrderEventRepository eventRepository;

    public GetOrderService(OrderRepository orderRepository,
                           OrderEventRepository eventRepository) {
        this.orderRepository = orderRepository;
        this.eventRepository = eventRepository;
    }

    public Response getOrder(OrderId orderId, boolean includeEvents) {
        var order = orderRepository.getOrThrow(orderId);
        var events = includeEvents
                ? eventRepository.findByOrderId(orderId)
                : List.<OrderEvent>of();
        return new Response(order, events);
    }

    public record Response(Order order, List<OrderEvent> events) {}
}
