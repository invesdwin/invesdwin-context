package de.invesdwin.context.beans;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.annotation.concurrent.NotThreadSafe;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;

import de.invesdwin.context.log.error.Err;

@NotThreadSafe
public class AsyncAspectMethods {

    //CHECKSTYLE:OFF
    public volatile boolean asyncMethodStarted;
    public volatile boolean asyncMethodFinished;
    //CHECKSTYLE:ON

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
    public Future<?> futureAsyncMethod() {
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