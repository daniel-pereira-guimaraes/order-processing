package com.danielpgbrasil.orderprocessing.infrastructure.config;

import com.danielpgbrasil.orderprocessing.application.order.CreateOrderService;
import com.danielpgbrasil.orderprocessing.application.shared.AppTransaction;
import com.danielpgbrasil.orderprocessing.domain.order.OrderListener;
import com.danielpgbrasil.orderprocessing.domain.order.OrderRepository;
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
    private OrderListener orderListener;

    @Bean
    public CreateOrderService createOrderService() {
        return new CreateOrderService(transaction, orderRepository, orderListener);
    }
}
