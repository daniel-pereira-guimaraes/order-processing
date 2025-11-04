package com.danielpgbrasil.orderprocessing.application.order;

import com.danielpgbrasil.orderprocessing.application.shared.AppTransaction;
import com.danielpgbrasil.orderprocessing.domain.order.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StartOrderTransitService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StartOrderTransitService.class);

    private final AppTransaction transaction;
    private final OrderRepository repository;

    public StartOrderTransitService(AppTransaction transaction,
                                    OrderRepository repository) {
        this.transaction = transaction;
        this.repository = repository;
    }

    public void startTransit(OrderId orderId) {
        transaction.execute(() -> {
            var order = repository.getOrThrow(orderId);
            if (order.status() != OrderStatus.PICKING) {
                LOGGER.info("Ignorando pedido com status diferente de PICKING: orderId={}", orderId.value());
                return;
            }
            order.startTransit();
            repository.save(order);
            LOGGER.info("Pedido em transporte: orderId={}", orderId.value());
        });
    }
}
