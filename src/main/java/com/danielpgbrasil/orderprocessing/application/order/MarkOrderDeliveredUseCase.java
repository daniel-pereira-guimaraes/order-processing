package com.danielpgbrasil.orderprocessing.application.order;

import com.danielpgbrasil.orderprocessing.application.shared.AppTransaction;
import com.danielpgbrasil.orderprocessing.domain.order.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MarkOrderDeliveredUseCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(MarkOrderDeliveredUseCase.class);

    private final AppTransaction transaction;
    private final OrderRepository repository;

    public MarkOrderDeliveredUseCase(AppTransaction transaction,
                                     OrderRepository repository) {
        this.transaction = transaction;
        this.repository = repository;
    }

    public void markDelivered(OrderId orderId) {
        transaction.execute(() -> {
            var order = repository.getOrThrow(orderId);
            if (order.status() != OrderStatus.IN_TRANSIT) {
                LOGGER.info("Ignorando pedido {} com status diferente de IN_TRANSIT.", orderId.value());
                return;
            }
            order.markDelivered();
            repository.save(order);
            LOGGER.info("Pedido {} entregue.", orderId.value());
        });
    }
}
