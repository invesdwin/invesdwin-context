package de.invesdwin.context.integration;

import java.util.function.Supplier;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import org.apache.commons.lang3.BooleanUtils;

import de.invesdwin.util.concurrent.RetryThreads;
import io.netty.util.concurrent.FastThreadLocal;

@ThreadSafe
public final class DatabaseThreads {

    private static final FastThreadLocal<Boolean> THREAD_BLOCKING_UPDATE_DATABASE_DISABLED = new FastThreadLocal<>();
    @GuardedBy("explcitly not volatile since cached information per thread is fine")
    private static boolean registerThreadBlockingUpdateDatabaseDisabledUsed;

    private DatabaseThreads() {}

    public static boolean isThreadBlockingUpdateDatabaseDisabled() {
        if (registerThreadBlockingUpdateDatabaseDisabledUsed) {
            //existing setting has priority over isThreadRetryDisabled
            final Boolean existing = THREAD_BLOCKING_UPDATE_DATABASE_DISABLED.get();
            if (existing != null) {
                return existing;
            }
        }
        return RetryThreads.isThreadRetryDisabledDefault();
    }

    public static Boolean registerThreadBlockingUpdateDatabaseDisabled() {
        return registerThreadBlockingUpdateDatabaseDisabled(true);
    }

    public static Boolean registerThreadBlockingUpdateDatabaseDisabled(
            final boolean threadBlockingUpdateDatabaseDisabled) {
        final boolean threadBlockingUpdateDatabaseDisabledBefore = registerThreadBlockingUpdateDatabaseDisabledUsed
                && BooleanUtils.isTrue(THREAD_BLOCKING_UPDATE_DATABASE_DISABLED.get());
        if (threadBlockingUpdateDatabaseDisabledBefore != threadBlockingUpdateDatabaseDisabled) {
            THREAD_BLOCKING_UPDATE_DATABASE_DISABLED.set(threadBlockingUpdateDatabaseDisabled);
            registerThreadBlockingUpdateDatabaseDisabledUsed = true;
            return threadBlockingUpdateDatabaseDisabledBefore;
        } else {
            return null;
        }
    }

    public static void unregisterThreadBlockingUpdateDisabled(
            final Boolean registerThreadBlockingUpdateDatabaseDisabled) {
        if (registerThreadBlockingUpdateDatabaseDisabled == null) {
            //nothing to do since we did not change anything
            return;
        }
        //restore before state
        if (!registerThreadBlockingUpdateDatabaseDisabled) {
            THREAD_BLOCKING_UPDATE_DATABASE_DISABLED.remove();
        } else {
            THREAD_BLOCKING_UPDATE_DATABASE_DISABLED.set(true);
        }
    }

    public static <T> T callBlockingAll(final Supplier<T> supplier) {
        final Boolean registerThreadBlockingUpdateDatabaseDisabled = DatabaseThreads
                .registerThreadBlockingUpdateDatabaseDisabled(false);
        final Boolean registerThreadRetryDisabled = RetryThreads.registerThreadRetryDisabled(false);
        try {
            return supplier.get();
        } finally {
            RetryThreads.unregisterThreadRetryDisabled(registerThreadRetryDisabled);
            DatabaseThreads.unregisterThreadBlockingUpdateDisabled(registerThreadBlockingUpdateDatabaseDisabled);
        }
    }

    public static void callBlockingAll(final Runnable runnable) {
        callBlockingAll(() -> {
            runnable.run();
            return null;
        });
    }

}
