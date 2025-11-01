package com.danielpgbrasil.orderprocessing.domain.order;

public interface OrderListener {
    void statusChanged(Order order);
}
