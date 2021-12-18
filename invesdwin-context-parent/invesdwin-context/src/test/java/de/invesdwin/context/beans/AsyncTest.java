package de.invesdwin.context.beans;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.annotation.concurrent.ThreadSafe;

import org.junit.jupiter.api.Test;

import de.invesdwin.context.test.ATest;
import de.invesdwin.util.assertions.Assertions;

@ThreadSafe
public class AsyncTest extends ATest {

    @Test
    public void testAsyncMethod() throws InterruptedException {
        final AsyncAspectMethods m = new AsyncAspectMethods();
        Assertions.assertThat(m.asyncMethodStarted).isFalse();
        Assertions.assertThat(m.asyncMethodFinished).isFalse();
        m.asyncMethod();
        TimeUnit.MILLISECONDS.sleep(100);
        Assertions.assertThat(m.asyncMethodStarted).isTrue();
        Assertions.assertThat(m.asyncMethodFinished).isFalse();
        TimeUnit.SECONDS.sleep(1);
        Assertions.assertThat(m.asyncMethodStarted).isTrue();
        Assertions.assertThat(m.asyncMethodFinished).isTrue();
    }

    @Test
    public void testFutureAsyncMethod() throws InterruptedException, ExecutionException {
        final AsyncAspectMethods m = new AsyncAspectMethods();
        Assertions.assertThat(m.asyncMethodStarted).isFalse();
        Assertions.assertThat(m.asyncMethodFinished).isFalse();
        final Future<?> future = m.futureAsyncMethod();
        Assertions.assertThat(future.isDone()).isFalse();
        TimeUnit.MILLISECONDS.sleep(100);
        Assertions.assertThat(m.asyncMethodStarted).isTrue();
        Assertions.assertThat(m.asyncMethodFinished).isFalse();
        Assertions.assertThat(future.isDone()).isFalse();
        TimeUnit.SECONDS.sleep(1);
        Assertions.assertThat(m.asyncMethodStarted).isTrue();
        Assertions.assertThat(m.asyncMethodFinished).isTrue();
        Assertions.assertThat(future.isDone()).isTrue();
        Assertions.assertThat(future.get()).isNotNull();
    }

}
