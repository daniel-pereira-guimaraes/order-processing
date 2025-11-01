package com.danielpgbrasil.orderprocessing.application.shared;

public interface AppTransaction {
    boolean inTransaction();
    void execute(Runnable runnable);
}
