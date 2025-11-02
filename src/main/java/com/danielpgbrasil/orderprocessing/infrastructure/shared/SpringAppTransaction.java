package com.danielpgbrasil.orderprocessing.infrastructure.shared;

import com.danielpgbrasil.orderprocessing.application.shared.AppTransaction;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;

@Component
public class SpringAppTransaction implements AppTransaction {

    private final PlatformTransactionManager transactionManager;

    public SpringAppTransaction(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Override
    public boolean inTransaction() {
        return TransactionSynchronizationManager.isActualTransactionActive();
    }

    @Override
    public void execute(Runnable runnable) {
        var template = new TransactionTemplate(transactionManager);
        template.executeWithoutResult(status -> runnable.run());
    }
}
