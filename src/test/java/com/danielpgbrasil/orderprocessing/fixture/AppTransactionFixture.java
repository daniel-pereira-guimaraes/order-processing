package com.danielpgbrasil.orderprocessing.fixture;

import com.danielpgbrasil.orderprocessing.application.shared.AppTransaction;
import org.mockito.stubbing.Stubber;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

public class AppTransactionFixture {

    private AppTransactionFixture() {
    }

    public static AppTransaction mockedTransaction() {
        var transaction = mock(AppTransaction.class);
        doAnswer(invocationOnMock -> {
            when(transaction.inTransaction()).thenReturn(true);
            Runnable runnable = invocationOnMock.getArgument(0);
            runnable.run();
            when(transaction.inTransaction()).thenReturn(false);
            return null;
        }).when(transaction).execute(any());
        return transaction;
    }

    public static Stubber assertThatInTransaction(AppTransaction transaction) {
        return doAnswer(invocationOnMock -> {
            assertThat(transaction.inTransaction(), is(true));
            return null;
        });
    }
}
