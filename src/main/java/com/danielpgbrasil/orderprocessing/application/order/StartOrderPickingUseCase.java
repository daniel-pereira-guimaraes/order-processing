package com.danielpgbrasil.orderprocessing.application.order;

import com.danielpgbrasil.orderprocessing.application.shared.AppTransaction;
import com.danielpgbrasil.orderprocessing.domain.order.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StartOrderPickingUseCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(StartOrderPickingUseCase.class);

    private final AppTransaction transaction;
    private final OrderRepository repository;

    public StartOrderPickingUseCase(AppTransaction transaction,
                                    OrderRepository repository) {
        this.transaction = transaction;
        this.repository = repository;
    }

    public void startPicking(OrderId orderId) {
        transaction.execute(() -> {
            var order = repository.getOrThrow(orderId);
            if (order.status() != OrderStatus.CREATED) {
                LOGGER.info("Ignorando pedido {} com status diferente de CREATED.", orderId.value());
                return;
            }
            order.startPicking();
            repository.save(order);
            LOGGER.info("Pedido {} em seperação.", orderId.value());
        });
    }
}
