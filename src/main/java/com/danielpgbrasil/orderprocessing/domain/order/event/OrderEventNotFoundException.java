package com.danielpgbrasil.orderprocessing.domain.order.event;

import com.danielpgbrasil.orderprocessing.domain.shared.AbstractNotFoundException;

public class OrderEventNotFoundException extends AbstractNotFoundException {

    public OrderEventNotFoundException(OrderEventId id) {
        super("Evento de pedido não encontrado: " + id.value());
    }
}
