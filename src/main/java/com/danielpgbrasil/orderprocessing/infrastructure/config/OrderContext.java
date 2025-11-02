package com.danielpgbrasil.orderprocessing.infrastructure.config;

import com.danielpgbrasil.orderprocessing.application.order.CreateOrderService;
import com.danielpgbrasil.orderprocessing.application.order.GetOrderService;
import com.danielpgbrasil.orderprocessing.application.order.GetOrderStatusService;
import com.danielpgbrasil.orderprocessing.application.shared.AppTransaction;
import com.danielpgbrasil.orderprocessing.domain.order.OrderListener;
import com.danielpgbrasil.orderprocessing.domain.order.OrderRepository;
import com.danielpgbrasil.orderprocessing.domain.order.event.OrderEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderContext {

    @Autowired
    private AppTransaction transaction;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderEventRepository orderEventRepository;

    @Autowired
    private OrderListener orderListener;

    @Bean
    public CreateOrderService createOrderService() {
        return new CreateOrderService(transaction, orderRepository, orderListener);
    }

    @Bean
    public GetOrderService getOrderService() {
        return new GetOrderService(orderRepository, orderEventRepository);
    }

    @Bean
    public GetOrderStatusService getOrderStatusService() {
        return new GetOrderStatusService(orderRepository);
    }
}
