package com.danielpgbrasil.orderprocessing.infrastructure;

import com.danielpgbrasil.orderprocessing.domain.order.Order;
import com.danielpgbrasil.orderprocessing.domain.order.OrderListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class OrderListenerImpl implements OrderListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderListenerImpl.class);

    @Override
    public void statusChanged(Order order) {
        LOGGER.info("Status do pedido alterado: orderId={}, status={}", order.id().value(), order.status());
    }
}
