package de.invesdwin.context.beans;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.annotation.concurrent.ThreadSafe;

import org.junit.Test;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;

import de.invesdwin.context.log.error.Err;
import de.invesdwin.context.test.ATest;
import de.invesdwin.util.assertions.Assertions;

@ThreadSafe
public class AsyncTest extends ATest {

    private volatile boolean asyncMethodStarted;
    private volatile boolean asyncMethodFinished;

    @Test
    public void testAsyncMethod() throws InterruptedException {
        final AsyncAspectMethods m = new AsyncAspectMethods();
        Assertions.assertThat(asyncMethodStarted).isFalse();
        Assertions.assertThat(asyncMethodFinished).isFalse();
        m.asyncMethod();
        TimeUnit.MILLISECONDS.sleep(100);
        Assertions.assertThat(asyncMethodStarted).isTrue();
        Assertions.assertThat(asyncMethodFinished).isFalse();
        TimeUnit.SECONDS.sleep(1);
        Assertions.assertThat(asyncMethodStarted).isTrue();
        Assertions.assertThat(asyncMethodFinished).isTrue();
    }

    @Test
    public void testFutureAsyncMethod() throws InterruptedException, ExecutionException {
        final AsyncAspectMethods m = new AsyncAspectMethods();
        Assertions.assertThat(asyncMethodStarted).isFalse();
        Assertions.assertThat(asyncMethodFinished).isFalse();
        final Future<?> future = m.futureAsyncMethod();
        Assertions.assertThat(future.isDone()).isFalse();
        TimeUnit.MILLISECONDS.sleep(100);
        Assertions.assertThat(asyncMethodStarted).isTrue();
        Assertions.assertThat(asyncMethodFinished).isFalse();
        Assertions.assertThat(future.isDone()).isFalse();
        TimeUnit.SECONDS.sleep(1);
        Assertions.assertThat(asyncMethodStarted).isTrue();
        Assertions.assertThat(asyncMethodFinished).isTrue();
        Assertions.assertThat(future.isDone()).isTrue();
        Assertions.assertThat(future.get()).isNotNull();
    }

    private class AsyncAspectMethods {

        @Async
        public void asyncMethod() {
            asyncMethodStarted = true;
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (final InterruptedException e) {
                throw Err.process(e);
            }
            asyncMethodFinished = true;
        }

        @Async
        private Future<?> futureAsyncMethod() {
            asyncMethodStarted = true;
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (final InterruptedException e) {
                throw Err.process(e);
            }
            asyncMethodFinished = true;
            return new AsyncResult<Object>(new Object());
        }
    }
}
