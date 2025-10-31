package com.danielpgbrasil.orderprocessing.domain.order;

import com.danielpgbrasil.orderprocessing.domain.shared.Validation;

import java.util.Collections;
import java.util.List;

public class OrderDetails {

    private final String customerName;
    private final String customerAddress;
    private final List<OrderItem> items;

    private OrderDetails(Builder builder) {
        this.customerName = Validation.requireNonBlank(
                builder.customerName, "O nome do cliente é requerido."
        );
        this.customerAddress = Validation.requireNonBlank(
                builder.customerAddress, "O endereço do cliente é requerido."
        );
        this.items = Collections.unmodifiableList(
                Validation.requireNonEmpty(builder.items, "Os itens do pedido são requeridos.")
        );
    }

    public String customerName() {
        return customerName;
    }

    public String customerAddress() {
        return customerAddress;
    }

    public List<OrderItem> items() {
        return items;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String customerName;
        private String customerAddress;
        private List<OrderItem> items;

        private Builder() {
        }

        public Builder withCustomerName(String customerName) {
            this.customerName = customerName;
            return this;
        }

        public Builder withCustomerAddress(String customerAddress) {
            this.customerAddress = customerAddress;
            return this;
        }

        public Builder withItems(List<OrderItem> items) {
            this.items = items;
            return this;
        }

        public OrderDetails build() {
            return new OrderDetails(this);
        }
    }
}
