package com.danielpgbrasil.orderprocessing.infrastructure.shared;

import com.danielpgbrasil.orderprocessing.domain.shared.AppClock;
import com.danielpgbrasil.orderprocessing.domain.shared.TimeMillis;
import org.springframework.stereotype.Component;

@Component
public class SystemClock implements AppClock {

    @Override
    public TimeMillis now() {
        return TimeMillis.now();
    }
}
