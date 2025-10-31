package com.danielpgbrasil.orderprocessing.domain.shared;

import java.util.Objects;

public abstract class SurrogateId {

    private final Long value;

    protected SurrogateId(Long value) {
        validate(value);
        this.value = value;
    }

    private void validate(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("O id é requerido.");
        }
        if (value <= 0) {
            throw new IllegalArgumentException("O id deve ser positivo.");
        }
    }

    public Long value() {
        return value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SurrogateId that = (SurrogateId) o;
        return Objects.equals(value, that.value);
    }
}
