package de.invesdwin.context.integration.retry;

import javax.annotation.concurrent.ThreadSafe;

import io.netty.util.concurrent.FastThreadLocal;

/**
 * The retry mechanism's own call-scope stack: every active ExceptionCauseRetryTemplate registration increments a
 * per-thread depth; the 'fallThroughToOuterRetry' option consults it to decide whether an outer retry already
 * encloses the current execution.
 *
 * <p>
 * Isolation is a properly balanced FastThreadLocal (try-with-resources restores the previous depth on close, so pool
 * thread reuse cannot leak state). Cross-thread coverage is EXPLICIT, never inferred from thread names or TaskInfo
 * decorations (those are display artifacts and are truncated): a submitting context that itself runs inside an active
 * retry can {@link #capture()} its depth and the task body {@link #install(int)}s it - this is used by the lazy data
 * updaters, whose task threads hold table write locks while retrying: the submitter's outer retry then becomes
 * visible to the task's fall-through sites, so refusals propagate (unwinding locks and the future) and the outer
 * retry re-attempts the whole operation from a clean state.
 */
@ThreadSafe
public final class RetryScopes {

    private static final FastThreadLocal<Integer> DEPTH = new FastThreadLocal<Integer>() {

        @Override
        protected Integer initialValue() {
            return Integer.valueOf(0);
        }
    };

    private RetryScopes() {}

    public static final class Registration implements AutoCloseable {

        private final int prevDepth;

        private Registration(final int prevDepth) {
            this.prevDepth = prevDepth;
        }

        @Override
        public void close() {
            DEPTH.set(Integer.valueOf(prevDepth));
        }
    }

    /**
     * Marks the current execution as an active retry scope on this thread.
     */
    public static Registration register() {
        final int prevDepth = DEPTH.get().intValue();
        DEPTH.set(Integer.valueOf(prevDepth + 1));
        return new Registration(prevDepth);
    }

    /**
     * Snapshot of the current thread's enclosing active-retry count.
     */
    public static int capture() {
        return DEPTH.get().intValue();
    }

    /**
     * Adds an inherited outer-retry count to the current thread for the duration of the returned registration
     * (see class javadoc; the previous depth is restored on close).
     */
    public static Registration install(final int inheritedOuterDepth) {
        final int prevDepth = DEPTH.get().intValue();
        DEPTH.set(Integer.valueOf(prevDepth + inheritedOuterDepth));
        return new Registration(prevDepth);
    }

    /**
     * True when an outer retry already encloses the current execution beyond the caller's own innermost level.
     */
    public static boolean hasOuterRetry() {
        return DEPTH.get().intValue() >= 2;
    }

    public static int currentDepth() {
        return DEPTH.get().intValue();
    }

}
