package de.invesdwin.context.integration.concurrent.nonblocking;

import java.util.concurrent.Future;

import de.invesdwin.util.concurrent.lambda.callable.ISafeCallable;
import de.invesdwin.util.time.duration.Duration;

public interface INonBlockingCallable<V> extends ISafeCallable<V>, INonBlockingBase {

    Future<V> maybeCall();

    V maybeCallAndGet();

    V maybeCallAndRetry();

    V getCallFuture(Future<V> future);

    V getCallFutureOrRetry(Future<V> future, Duration timeout);

}
