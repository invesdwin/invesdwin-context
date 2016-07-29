package de.invesdwin.context.beans.init;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.concurrent.NotThreadSafe;

import org.assertj.core.api.Fail;
import org.junit.ComparisonFailure;
import org.springframework.scheduling.annotation.Scheduled;

import de.invesdwin.aspects.annotation.SkipParallelExecution;
import de.invesdwin.util.assertions.Assertions;

@NotThreadSafe
class ScheduledTestBean {

    private volatile boolean scheduleOnceCalled;
    private volatile boolean blockingCalled;
    private volatile boolean synchronizedCalled;
    private final AtomicInteger scheduleOftenParalel = new AtomicInteger();
    private final AtomicInteger scheduleOftenCount = new AtomicInteger();

    public void testScheduled() throws InterruptedException {
        Thread.sleep(1);
        Assertions.assertThat(blockingCalled).isTrue();
        Assertions.assertThat(synchronizedCalled).isFalse(); //assertion aspect should prevent this coding issue to work at all
        scheduleBlocking(); //not throwing assetion, because execution is skipped
        Assertions.assertThat(scheduleOnceCalled).isTrue();
        Thread.sleep(1);
        Assertions.assertThat(scheduleOftenParalel.get()).isEqualTo(1);
        Thread.sleep(300);
        Assertions.assertThat(scheduleOftenCount.get()).isGreaterThan(2);
        try {
            scheduleSynchronized();
            Fail.fail("Exception expected");
        } catch (final ComparisonFailure e) {
            Assertions.assertThat(e.getMessage())
            .contains("@" + Scheduled.class.getSimpleName())
            .contains("@" + SkipParallelExecution.class.getSimpleName());
        }
    }

    @SuppressWarnings("unused" /* called by scheduler */)
    @Scheduled(fixedRate = Long.MAX_VALUE)
    private void scheduleOnce() {
        scheduleOnceCalled = true;
    }

    @SuppressWarnings("unused" /* called by scheduler */)
    @Scheduled(fixedRate = 10)
    private void scheduleOftenSequencial() throws InterruptedException {
        scheduleOftenCount.incrementAndGet();
        Assertions.assertThat(scheduleOftenParalel.incrementAndGet()).isEqualTo(1);
        TimeUnit.MILLISECONDS.sleep(100);
        Assertions.assertThat(scheduleOftenParalel.decrementAndGet()).isEqualTo(0);
    }

    @SuppressWarnings("unused" /* called by scheduler */)
    @Scheduled(fixedRate = 1)
    @SkipParallelExecution
    private void scheduleBlocking() {
        Assertions.assertThat(blockingCalled).isFalse();
        blockingCalled = true;
        try {
            TimeUnit.DAYS.sleep(Long.MAX_VALUE);
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @SuppressWarnings("unused" /* called by scheduler */)
    @Scheduled(fixedRate = Long.MAX_VALUE)
    private synchronized void scheduleSynchronized() {
        synchronizedCalled = true;
    }

}
