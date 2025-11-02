package com.danielpgbrasil.orderprocessing.domain.shared;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Objects;
import java.util.Optional;

public class TimeMillis  {

    private final Long value;

    private TimeMillis(Long value) {
        this.value = Objects.requireNonNull(value);
    }

    public static TimeMillis of(Long value) {
        return new TimeMillis(value);
    }

    public static Optional<TimeMillis> ofNullable(Long value) {
        return value == null ? Optional.empty()
                : Optional.of(new TimeMillis(value));
    }

    public static TimeMillis now() {
        return new TimeMillis(System.currentTimeMillis());
    }

    public Long value() {
        return value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof TimeMillis otherTimeMillis
                && Objects.equals(value, otherTimeMillis.value);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
