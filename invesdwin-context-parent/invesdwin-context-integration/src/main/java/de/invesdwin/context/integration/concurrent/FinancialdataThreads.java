package de.invesdwin.context.integration.concurrent;

import java.util.function.Supplier;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import org.apache.commons.lang3.BooleanUtils;

import de.invesdwin.util.concurrent.RetryThreads;
import de.invesdwin.util.math.Booleans;
import io.netty.util.concurrent.FastThreadLocal;

@ThreadSafe
public final class FinancialdataThreads {

    private static final FastThreadLocal<Boolean> THREAD_BLOCKING_UPDATE_CACHE_DISABLED = new FastThreadLocal<>();
    @GuardedBy("explcitly not volatile since cached information per thread is fine")
    private static boolean registerThreadBlockingUpdateCacheDisabledUsed;
    private static final FastThreadLocal<Boolean> THREAD_BLOCKING_UPDATE_SUBSCRIPTION_DISABLED = new FastThreadLocal<>();
    @GuardedBy("explcitly not volatile since cached information per thread is fine")
    private static boolean registerThreadBlockingUpdateSubscriptionDisabledUsed;

    private FinancialdataThreads() {}

    public static boolean isThreadBlockingUpdateDatabaseDisabled() {
        if (DatabaseThreads.isThreadBlockingUpdateDatabaseDisabled()) {
            return true;
        }
        return RetryThreads.isThreadRetryDisabled();
    }

    public static Boolean registerThreadBlockingUpdateDatabaseDisabled() {
        return DatabaseThreads.registerThreadBlockingUpdateDatabaseDisabled();
    }

    public static Boolean registerThreadBlockingUpdateDatabaseDisabled(
            final boolean threadBlockingUpdateDatabaseDisabled) {
        return DatabaseThreads.registerThreadBlockingUpdateDatabaseDisabled(threadBlockingUpdateDatabaseDisabled);
    }

    public static void unregisterThreadBlockingUpdateDatabaseDisabled(
            final Boolean registerThreadBlockingUpdateDatabaseDisabled) {
        DatabaseThreads.unregisterThreadBlockingUpdateDatabaseDisabled(registerThreadBlockingUpdateDatabaseDisabled);
    }

    public static boolean isThreadBlockingUpdateCacheDisabled() {
        if (registerThreadBlockingUpdateCacheDisabledUsed) {
            //existing setting has priority over isThreadRetryDisabled
            final Boolean existing = THREAD_BLOCKING_UPDATE_CACHE_DISABLED.get();
            if (existing != null) {
                return existing;
            }
        }
        return RetryThreads.isThreadRetryDisabled();
    }

    public static Boolean registerThreadBlockingUpdateCacheDisabled() {
        return registerThreadBlockingUpdateCacheDisabled(true);
    }

    public static Boolean registerThreadBlockingUpdateCacheDisabled(final boolean threadBlockingUpdateCacheDisabled) {
        final boolean threadBlockingUpdateCacheDisabledBefore = registerThreadBlockingUpdateCacheDisabledUsed
                && BooleanUtils.isTrue(THREAD_BLOCKING_UPDATE_CACHE_DISABLED.get());
        if (threadBlockingUpdateCacheDisabledBefore != threadBlockingUpdateCacheDisabled) {
            THREAD_BLOCKING_UPDATE_CACHE_DISABLED.set(threadBlockingUpdateCacheDisabled);
            registerThreadBlockingUpdateCacheDisabledUsed = true;
            return threadBlockingUpdateCacheDisabledBefore;
        } else {
            return null;
        }
    }

    public static void unregisterThreadBlockingUpdateCacheDisabled(
            final Boolean registerThreadBlockingUpdateCacheDisabled) {
        if (registerThreadBlockingUpdateCacheDisabled == null) {
            //nothing to do since we did not change anything
            return;
        }
        //restore before state
        if (!registerThreadBlockingUpdateCacheDisabled) {
            THREAD_BLOCKING_UPDATE_CACHE_DISABLED.remove();
        } else {
            THREAD_BLOCKING_UPDATE_CACHE_DISABLED.set(true);
        }
    }

    public static boolean isThreadBlockingUpdateSubscriptionDisabled() {
        if (registerThreadBlockingUpdateSubscriptionDisabledUsed) {
            //existing setting has priority over isThreadRetryDisabled
            final Boolean existing = THREAD_BLOCKING_UPDATE_SUBSCRIPTION_DISABLED.get();
            if (existing != null) {
                return existing;
            }
        }
        return RetryThreads.isThreadRetryDisabled();
    }

    public static Boolean registerThreadBlockingUpdateSubscriptionDisabled() {
        return registerThreadBlockingUpdateSubscriptionDisabled(true);
    }

    public static Boolean registerThreadBlockingUpdateSubscriptionDisabled(
            final boolean threadBlockingUpdateSubscriptionDisabled) {
        final boolean threadBlockingUpdateSubscriptionDisabledBefore = registerThreadBlockingUpdateSubscriptionDisabledUsed
                && BooleanUtils.isTrue(THREAD_BLOCKING_UPDATE_SUBSCRIPTION_DISABLED.get());
        if (threadBlockingUpdateSubscriptionDisabledBefore != threadBlockingUpdateSubscriptionDisabled) {
            THREAD_BLOCKING_UPDATE_SUBSCRIPTION_DISABLED.set(threadBlockingUpdateSubscriptionDisabled);
            registerThreadBlockingUpdateSubscriptionDisabledUsed = true;
            return threadBlockingUpdateSubscriptionDisabledBefore;
        } else {
            return null;
        }
    }

    public static void unregisterThreadBlockingUpdateSubscriptionDisabled(
            final Boolean registerThreadBlockingUpdateSubscriptionDisabled) {
        if (registerThreadBlockingUpdateSubscriptionDisabled == null) {
            //nothing to do since we did not change anything
            return;
        }
        //restore before state
        if (!registerThreadBlockingUpdateSubscriptionDisabled) {
            THREAD_BLOCKING_UPDATE_SUBSCRIPTION_DISABLED.remove();
        } else {
            THREAD_BLOCKING_UPDATE_SUBSCRIPTION_DISABLED.set(true);
        }
    }

    public static <T> T callBlockingAll(final Supplier<T> supplier) {
        //make sure initialization is not skipped or partial
        final Boolean registerThreadBlockingUpdateCacheDisabled = FinancialdataThreads
                .registerThreadBlockingUpdateCacheDisabled(false);
        final Boolean registerThreadBlockingUpdateSubscriptionDisabled = FinancialdataThreads
                .registerThreadBlockingUpdateSubscriptionDisabled(false);
        final Boolean registerThreadBlockingUpdateDatabaseDisabled = FinancialdataThreads
                .registerThreadBlockingUpdateDatabaseDisabled(false);
        final Boolean registerThreadRetryDisabled = RetryThreads.registerThreadRetryDisabled(false);
        try {
            return supplier.get();
        } finally {
            RetryThreads.unregisterThreadRetryDisabled(registerThreadRetryDisabled);
            FinancialdataThreads
                    .unregisterThreadBlockingUpdateDatabaseDisabled(registerThreadBlockingUpdateDatabaseDisabled);
            FinancialdataThreads.unregisterThreadBlockingUpdateSubscriptionDisabled(
                    registerThreadBlockingUpdateSubscriptionDisabled);
            FinancialdataThreads.unregisterThreadBlockingUpdateCacheDisabled(registerThreadBlockingUpdateCacheDisabled);
        }
    }

    public static void callBlockingAll(final Runnable runnable) {
        callBlockingAll(() -> {
            runnable.run();
            return null;
        });
    }

    public static void callMaybeUpdateBlockingCaches(final boolean initializedAndDataLoaded,
            final Runnable maybeUpdateRunnable) {
        //make sure intermediate caches don't skip their updates, else we might get incorrect/partial updates
        final Boolean registerThreadBlockingUpdateCacheDisabled = FinancialdataThreads
                .registerThreadBlockingUpdateCacheDisabled(false);
        final Boolean registerThreadBlockingUpdateSubscriptionDisabled;
        final Boolean registerThreadBlockingUpdateDatabaseDisabled;
        final Boolean registerThreadRetryDisabled;
        if (initializedAndDataLoaded) {
            if (Booleans.isTrue(registerThreadBlockingUpdateCacheDisabled)) {
                //though make subscription update non-blocking to make the updates still lazy
                registerThreadBlockingUpdateSubscriptionDisabled = FinancialdataThreads
                        .registerThreadBlockingUpdateSubscriptionDisabled();
                registerThreadBlockingUpdateDatabaseDisabled = FinancialdataThreads
                        .registerThreadBlockingUpdateDatabaseDisabled();
            } else {
                registerThreadBlockingUpdateSubscriptionDisabled = null;
                registerThreadBlockingUpdateDatabaseDisabled = null;
            }
            registerThreadRetryDisabled = null;
        } else {
            //make sure initialization is blocking
            registerThreadBlockingUpdateSubscriptionDisabled = FinancialdataThreads
                    .registerThreadBlockingUpdateSubscriptionDisabled(false);
            registerThreadBlockingUpdateDatabaseDisabled = FinancialdataThreads
                    .registerThreadBlockingUpdateDatabaseDisabled(false);
            registerThreadRetryDisabled = RetryThreads.registerThreadRetryDisabled(false);
        }
        try {
            maybeUpdateRunnable.run();
        } finally {
            RetryThreads.unregisterThreadRetryDisabled(registerThreadRetryDisabled);
            FinancialdataThreads
                    .unregisterThreadBlockingUpdateDatabaseDisabled(registerThreadBlockingUpdateDatabaseDisabled);
            FinancialdataThreads.unregisterThreadBlockingUpdateSubscriptionDisabled(
                    registerThreadBlockingUpdateSubscriptionDisabled);
            FinancialdataThreads.unregisterThreadBlockingUpdateCacheDisabled(registerThreadBlockingUpdateCacheDisabled);
        }
    }

}
