package de.invesdwin.context.integration.retry;

import javax.annotation.concurrent.ThreadSafe;

import org.junit.jupiter.api.Test;

import de.invesdwin.util.assertions.Assertions;

@ThreadSafe
public class RetryLaterPendingTest {

    @Test
    public void testRenderedMessageSeparatesTagFromText() {
        //the staging log shape was '[PENDING]Strategy is currently initializing...' - the separating space belongs to
        //the rendering prefix, so every producer concatenates PENDING_PREFIX (not the bare tag) with its message
        Assertions.checkEquals("[PENDING]", RetryLaterPendingRuntimeException.PENDING_TAG);
        Assertions.checkEquals("[PENDING] ", RetryLaterPendingRuntimeException.PENDING_PREFIX);
        final String rendered = RetryLaterPendingRuntimeException.PENDING_PREFIX
                + "Strategy is currently initializing: true";
        Assertions.checkEquals("[PENDING] Strategy is currently initializing: true", rendered);
        Assertions.checkTrue(RetryLaterPendingRuntimeException.isPending(new RuntimeException(rendered)));
    }

    @Test
    public void testTagOfProducersCompiledEarlierIsStillRecognised() {
        //a public constant is inlined at compile time: a producer built before the space was added keeps emitting
        //'[PENDING]Strategy...' over the wire, and its attempts must still count as proven in-flight work
        Assertions.checkTrue(RetryLaterPendingRuntimeException
                .isPending(new RuntimeException("[PENDING]Strategy is currently initializing: true")));
        Assertions.checkTrue(RetryLaterPendingRuntimeException
                .isPending(new RuntimeException(new RetryLaterPendingException("update lock is beaten"))));
        Assertions.checkFalse(RetryLaterPendingRuntimeException.isPending(new RuntimeException("no marker")));
        Assertions.checkFalse(RetryLaterPendingRuntimeException.isPending(null));
    }

}
