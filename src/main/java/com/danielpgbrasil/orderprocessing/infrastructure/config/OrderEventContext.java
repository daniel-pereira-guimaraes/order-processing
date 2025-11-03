package com.danielpgbrasil.orderprocessing.infrastructure.config;

import com.danielpgbrasil.orderprocessing.application.order.event.CreateOrderEventService;
import com.danielpgbrasil.orderprocessing.application.order.event.GetOrderEventsService;
import com.danielpgbrasil.orderprocessing.application.order.event.OrderEventPublisher;
import com.danielpgbrasil.orderprocessing.application.order.event.PublishPendingOrderEventsService;
import com.danielpgbrasil.orderprocessing.application.shared.AppTransaction;
import com.danielpgbrasil.orderprocessing.domain.order.OrderRepository;
import com.danielpgbrasil.orderprocessing.domain.order.event.OrderEventRepository;
import com.danielpgbrasil.orderprocessing.domain.shared.AppClock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderEventContext {

    @Autowired
    private AppTransaction transaction;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderEventRepository orderEventRepository;

    @Autowired
    private AppClock clock;

    @Autowired
    private OrderEventPublisher orderEventPublisher;

    @Bean
    public GetOrderEventsService getOrderEventsService() {
        return new GetOrderEventsService(orderRepository, orderEventRepository);
    }

    @Bean
    public CreateOrderEventService createOrderEventService() {
        return new CreateOrderEventService(transaction, orderEventRepository, clock);
    }

    @Bean
    public PublishPendingOrderEventsService publishPendingOrderEventsService() {
        return new PublishPendingOrderEventsService(transaction, orderEventRepository, orderEventPublisher);
    }

}
