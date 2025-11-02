package com.danielpgbrasil.orderprocessing.domain.order;

import com.danielpgbrasil.orderprocessing.domain.shared.SurrogateId;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Optional;

public class OrderId extends SurrogateId {

    protected OrderId(Long value) {
        super(value);
    }

    public static OrderId of(Long value) {
        return new OrderId(value);
    }

    public static Optional<OrderId> ofNullable(Long value) {
        return value == null ? Optional.empty()
                : Optional.of(new OrderId(value));
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
