package de.invesdwin.context.integration.concurrent.nonblocking;

import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.concurrent.Immutable;

import org.junit.jupiter.api.Test;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.factory.ILockCollectionFactory;
import de.invesdwin.util.concurrent.Executors;
import de.invesdwin.util.concurrent.WrappedExecutorService;
import de.invesdwin.util.concurrent.future.Futures;
import de.invesdwin.util.concurrent.lock.readwrite.IReentrantReadWriteLock;
import de.invesdwin.util.time.date.FTimeUnit;
import de.invesdwin.util.time.duration.Duration;

/**
 * A tier that dispatches work to its own pool and then waits for it has no way to know that the waiting thread already
 * owns a lock the dispatched worker needs. A store update attempt is exactly that: it holds the key's write lock, and
 * the demand its source raises while the attempt runs reads the same key. The dispatched worker parks on the read
 * lock, the attempt parks on the worker's future, and neither moves again - the wait in that branch has no timeout, so
 * nothing converts it into a retry. A crawler pass that dies this way never finishes, which turns a startup state into
 * a permanent refusal for every instrument behind it.
 * <p>
 * The lock here is the same type the store's table lock is (a read-write lock from the house lock factory), and the
 * write-then-read pair is the same one the update attempt and its demand perform.
 * <p>
 * Red first: without the inline branch the first two cells time out, because the nested demand's worker is parked
 * behind the write lock its own dispatcher holds.
 */
@Immutable
public class NonBlockingDispatchCycleTest {

    private static final Duration WAIT_BOUND = new Duration(10, FTimeUnit.SECONDS);

    private final IReentrantReadWriteLock keyLock = ILockCollectionFactory.getInstance(true)
            .newReadWriteLock(NonBlockingDispatchCycleTest.class.getSimpleName());

    @Test
    public void aDemandRaisedInsideADispatchedCallRunsOnThatThreadAndNotOnAPoolWorker() throws Exception {
        final WrappedExecutorService probe = newProbe();
        try {
            final String result = Futures.getNoInterrupt(probe.submit(() -> {
                return new LockHolder().maybeCallAndGet();
            }), WAIT_BOUND);
            final int separator = result.indexOf('|');
            final String readerThread = result.substring(0, separator);
            final String holderThread = result.substring(separator + 1);
            Assertions.assertThat(readerThread)
                    .as("the nested demand ran on the thread that holds the write lock; a dispatched worker would "
                            + "park behind that lock while the holder waits on its future")
                    .isEqualTo(holderThread);
        } catch (final TimeoutException e) {
            Assertions.fail("the nested demand never returned: it was dispatched to a pool worker parked behind the "
                    + "write lock its own dispatcher holds - the cycle the crawler startup pass dies in");
        } finally {
            probe.shutdownNow();
        }
    }

    @Test
    public void aRunRequestedInsideADispatchedCallRunsOnThatThreadAndNotOnAPoolWorker() throws Exception {
        final AtomicReference<String> holderThread = new AtomicReference<>();
        final AtomicReference<String> runnerThread = new AtomicReference<>();
        final WrappedExecutorService probe = newProbe();
        try {
            Futures.getNoInterrupt(probe.submit(() -> {
                new LockHoldingRun(holderThread, runnerThread).maybeRunAndWait();
                return null;
            }), WAIT_BOUND);
            Assertions.assertThat(runnerThread.get())
                    .as("the nested run executed on the write-lock holder instead of on a pool worker behind it")
                    .isEqualTo(holderThread.get());
        } catch (final TimeoutException e) {
            Assertions.fail("the nested run never returned: it was dispatched to a pool worker parked behind the "
                    + "write lock its own dispatcher holds");
        } finally {
            probe.shutdownNow();
        }
    }

    @Test
    public void aDemandFromAnOrdinaryBlockingThreadStillRunsOnAPoolWorker() {
        final String callerThread = Thread.currentThread().getName();
        final String readerThread = new SameKeyReader().maybeCallAndGet();
        Assertions.assertThat(readerThread)
                .as("this thread is not itself a dispatched call, so the tier keeps dispatching - the branch that "
                        + "keeps a caller thread from being consumed by the demand is the point of the tier")
                        .isNotEqualTo(callerThread);
    }

    private static WrappedExecutorService newProbe() {
        return Executors.newFixedThreadPool(NonBlockingDispatchCycleTest.class.getSimpleName() + "_probe", 1);
    }

    private class LockHolder extends ANonBlockingCallable<String> {

        LockHolder() {
            super(LockHolder.class, "holdsTheWriteLock");
        }

        @Override
        public String call() {
            final String holderThread = Thread.currentThread().getName();
            keyLock.writeLock().lock();
            try {
                return new SameKeyReader().maybeCallAndGet() + "|" + holderThread;
            } finally {
                keyLock.writeLock().unlock();
            }
        }

    }

    private class SameKeyReader extends ANonBlockingCallable<String> {

        SameKeyReader() {
            super(SameKeyReader.class, "wantsTheReadLock");
        }

        @Override
        public String call() {
            keyLock.readLock().lock();
            try {
                return Thread.currentThread().getName();
            } finally {
                keyLock.readLock().unlock();
            }
        }

    }

    private class LockHoldingRun extends ANonBlockingRunnable {

        private final AtomicReference<String> holderThread;
        private final AtomicReference<String> runnerThread;

        LockHoldingRun(final AtomicReference<String> holderThread, final AtomicReference<String> runnerThread) {
            super(LockHoldingRun.class, "holdsTheWriteLock");
            this.holderThread = holderThread;
            this.runnerThread = runnerThread;
        }

        @Override
        public void run() {
            holderThread.set(Thread.currentThread().getName());
            keyLock.writeLock().lock();
            try {
                new SameKeyRun(runnerThread).maybeRunAndWait();
            } finally {
                keyLock.writeLock().unlock();
            }
        }

    }

    private class SameKeyRun extends ANonBlockingRunnable {

        private final AtomicReference<String> runnerThread;

        SameKeyRun(final AtomicReference<String> runnerThread) {
            super(SameKeyRun.class, "wantsTheReadLock");
            this.runnerThread = runnerThread;
        }

        @Override
        public void run() {
            keyLock.readLock().lock();
            try {
                runnerThread.set(Thread.currentThread().getName());
            } finally {
                keyLock.readLock().unlock();
            }
        }

    }

}
