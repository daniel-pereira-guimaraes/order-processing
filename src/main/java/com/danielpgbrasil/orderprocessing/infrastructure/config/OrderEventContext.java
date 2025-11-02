package com.danielpgbrasil.orderprocessing.infrastructure.config;

import com.danielpgbrasil.orderprocessing.application.order.event.GetOrderEventsService;
import com.danielpgbrasil.orderprocessing.domain.order.OrderRepository;
import com.danielpgbrasil.orderprocessing.domain.order.event.OrderEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderEventContext {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderEventRepository orderEventRepository;

    @Bean
    public GetOrderEventsService getOrderEventsService() {
        return new GetOrderEventsService(orderRepository, orderEventRepository);
    }

}
