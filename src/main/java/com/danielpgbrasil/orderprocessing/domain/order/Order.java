package com.danielpgbrasil.orderprocessing.domain.order;

import com.danielpgbrasil.orderprocessing.domain.shared.Validation;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Objects;

public class Order {

    private OrderId id;
    private final OrderDetails details;
    private OrderStatus status;
    private final transient OrderListener listener;

    private Order(Builder builder) {
        id = builder.id;
        details = Validation.required(builder.details, "Os detalhes do pedido são requeridos.");
        status = Validation.required(builder.status, "O status do pedido é requerido.");
        listener = Validation.required(builder.listener, "O listener é requerido.");
    }

    public OrderId id() {
        return id;
    }

    public OrderDetails details() {
        return details;
    }

    public OrderStatus status() {
        return status;
    }

    public void finalizeCreation(OrderId id) {
        if (this.id != null) {
            throw new IllegalStateException("A criação do pedido já foi finalizada.");
        }
        this.id = Validation.required(id, "O id é requerido.");
        updateStatus(OrderStatus.CREATED);
    }

    public void startPicking() {
        ensureCurrentStatus(OrderStatus.CREATED);
        updateStatus(OrderStatus.PICKING);
    }

    public void startTransit() {
        ensureCurrentStatus(OrderStatus.PICKING);
        updateStatus(OrderStatus.IN_TRANSIT);
    }

    public void markDelivered() {
        ensureCurrentStatus(OrderStatus.IN_TRANSIT);
        updateStatus(OrderStatus.DELIVERED);
    }

    private void ensureCurrentStatus(OrderStatus expected) {
        if (this.status != expected) {
            throw new IllegalStateException(
                    "Status inválido: esperado %s, mas está %s.".formatted(expected, this.status)
            );
        }
    }

    private void updateStatus(OrderStatus newStatus) {
        this.status = newStatus;
        this.listener.statusChanged(this);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        return equalsCasted((Order) other);
    }

    private boolean equalsCasted(Order other) {
        return Objects.equals(id, other.id)
                && Objects.equals(details, other.details)
                && status == other.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, details, status);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private OrderId id;
        private OrderDetails details;
        private OrderStatus status;
        private OrderListener listener;

        private Builder() {
        }

        public Builder withId(OrderId id) {
            this.id = id;
            return this;
        }

        public Builder withDetails(OrderDetails details) {
            this.details = details;
            return this;
        }

        public Builder withStatus(OrderStatus status) {
            this.status = status;
            return this;
        }

        public Builder withListener(OrderListener listener) {
            this.listener = listener;
            return this;
        }

        public Order build() {
            return new Order(this);
        }
    }
}
