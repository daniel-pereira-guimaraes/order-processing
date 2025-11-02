package com.danielpgbrasil.orderprocessing.domain.order;

import com.danielpgbrasil.orderprocessing.domain.shared.AbstractNotFoundException;

public class OrderNotFoundException extends AbstractNotFoundException {

    public OrderNotFoundException(OrderId id) {
        super("Pedido não encontrado: " + id.value());
    }
}
