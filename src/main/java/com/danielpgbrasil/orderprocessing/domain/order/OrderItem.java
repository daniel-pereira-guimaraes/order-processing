package com.danielpgbrasil.orderprocessing.domain.order;

import com.danielpgbrasil.orderprocessing.domain.shared.Validation;

import java.math.BigDecimal;
import java.util.Objects;

public class OrderItem {

    private static final int MIN_QUANTITY = 1;
    private static final int MAX_QUANTITY = 999;
    private static final BigDecimal MIN_PRICE = BigDecimal.ZERO;
    private static final BigDecimal MAX_PRICE = new BigDecimal("999999.99");

    private final Long productId;
    private final Integer quantity;
    private final BigDecimal price;

    private OrderItem(Builder builder) {
        this.productId = Validation.required(builder.productId, "O id do produto é requerido.");
        this.quantity = validateQuantity(builder.quantity);
        this.price = validatePrice(builder.price);
    }

    private static int validateQuantity(Integer quantity) {
        int nonNull = Validation.required(quantity, "A quantidade é requerida.");
        if (nonNull < MIN_QUANTITY || nonNull > MAX_QUANTITY) {
            throw new IllegalArgumentException(
                    "A quantidade deve ser de %d até %d.".formatted(MIN_QUANTITY, MAX_QUANTITY)
            );
        }
        return nonNull;
    }

    private static BigDecimal validatePrice(BigDecimal price) {
        var nonNull = Validation.required(price, "O preço é requerido.");
        if (nonNull.compareTo(MIN_PRICE) < 0 || nonNull.compareTo(MAX_PRICE) > 0) {
            throw new IllegalArgumentException(
                    "O preço deve ser de %s até %s".formatted(MIN_PRICE, MAX_PRICE)
            );
        }
        return nonNull;
    }

    public Long productId() {
        return productId;
    }

    public Integer quantity() {
        return quantity;
    }

    public BigDecimal price() {
        return price;
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, quantity, price);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        return equalsCasted((OrderItem) other);
    }

    private boolean equalsCasted(OrderItem other) {
        return Objects.equals(productId, other.productId)
                && Objects.equals(quantity, other.quantity)
                && Objects.equals(price, other.price);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Long productId;
        private Integer quantity;
        private BigDecimal price;

        private Builder() {
        }

        public Builder withProductId(Long productId) {
            this.productId = productId;
            return this;
        }

        public Builder withQuantity(Integer quantity) {
            this.quantity = quantity;
            return this;
        }

        public Builder withPrice(BigDecimal price) {
            this.price = price;
            return this;
        }

        public OrderItem build() {
            return new OrderItem(this);
        }
    }
}
