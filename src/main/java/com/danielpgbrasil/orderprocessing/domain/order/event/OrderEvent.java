package com.danielpgbrasil.orderprocessing.domain.order.event;

import com.danielpgbrasil.orderprocessing.domain.order.OrderId;
import com.danielpgbrasil.orderprocessing.domain.shared.TimeMillis;
import com.danielpgbrasil.orderprocessing.domain.shared.Validation;

import java.util.Objects;

public class OrderEvent {

    private OrderEventId id;
    private final OrderId orderId;
    private final OrderEventType type;
    private final TimeMillis createdAt;
    private boolean published;

    private OrderEvent(Builder builder) {
        id = builder.id;
        orderId = Validation.required(builder.orderId, "O id do pedido é requerido.");
        type = Validation.required(builder.type, "O tipo do evento é requerido.");
        createdAt = Validation.required(builder.createdAt, "O timestamp do evento é requerido.");
        published = Validation.required(builder.published, "Published é requerido.");
    }

    public OrderEventId id() {
        return id;
    }

    public OrderId orderId() {
        return orderId;
    }

    public OrderEventType type() {
        return type;
    }

    public TimeMillis createdAt() {
        return createdAt;
    }

    public boolean isPublished() {
        return published;
    }

    public void finalizeCreation(OrderEventId id) {
        if (this.id != null) {
            throw new IllegalStateException("A criação do evento já foi finalizada.");
        }
        this.id = Validation.required(id, "O id do evento é requerido.");
    }

    public void markAsPublished() {
        if (this.published) {
            throw new IllegalStateException("Evento já publicado.");
        }
        this.published = true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, orderId, type, createdAt, published);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        return equalsCasted((OrderEvent) other);
    }

    private boolean equalsCasted(OrderEvent other) {
        return Objects.equals(id, other.id)
                && Objects.equals(orderId, other.orderId)
                && type == other.type
                && Objects.equals(createdAt, other.createdAt)
                && Objects.equals(published, other.published);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private OrderEventId id;
        private OrderId orderId;
        private OrderEventType type;
        private TimeMillis createdAt;
        private Boolean published;

        private Builder() {
        }

        public Builder withId(OrderEventId id) {
            this.id = id;
            return this;
        }

        public Builder withOrderId(OrderId orderId) {
            this.orderId = orderId;
            return this;
        }

        public Builder withType(OrderEventType type) {
            this.type = type;
            return this;
        }

        public Builder withCreatedAt(TimeMillis createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder withPublished(Boolean published) {
            this.published = published;
            return this;
        }

        public OrderEvent build() {
            return new OrderEvent(this);
        }
    }

}
