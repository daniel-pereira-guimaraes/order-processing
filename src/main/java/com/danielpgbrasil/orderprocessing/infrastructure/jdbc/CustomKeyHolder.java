package com.danielpgbrasil.orderprocessing.infrastructure.jdbc;

import org.springframework.jdbc.support.GeneratedKeyHolder;

public class CustomKeyHolder extends GeneratedKeyHolder {

    public long asLong() {
        var key = this.getKey();
        if (key == null) {
            throw new IllegalStateException("Erro ao obter ID gerado pelo banco de dados.");
        }
        return key.longValue();
    }

}
