package de.invesdwin.context.integration.retry;

import javax.annotation.concurrent.ThreadSafe;

import org.junit.jupiter.api.Test;

import de.invesdwin.util.assertions.Assertions;

@ThreadSafe
public class RetryScopesTest {

    @Test
    public void testBalanceAndNesting() {
        Assertions.checkEquals(0, RetryScopes.currentDepth());
        try (RetryScopes.Registration outer = RetryScopes.register()) {
            Assertions.checkEquals(1, RetryScopes.currentDepth());
            Assertions.checkFalse(RetryScopes.hasOuterRetry());
            try (RetryScopes.Registration inner = RetryScopes.register()) {
                Assertions.checkEquals(2, RetryScopes.currentDepth());
                Assertions.checkTrue(RetryScopes.hasOuterRetry());
            }
            Assertions.checkEquals(1, RetryScopes.currentDepth());
        }
        Assertions.checkEquals(0, RetryScopes.currentDepth());
    }

    @Test
    public void testInheritanceRestoresDespiteThrowables() {
        //pool-thread reuse safety: an exception inside an installed scope must still restore the exact previous depth
        Assertions.checkEquals(0, RetryScopes.currentDepth());
        try {
            try (RetryScopes.Registration inherited = RetryScopes.install(1)) {
                try (RetryScopes.Registration own = RetryScopes.register()) {
                    Assertions.checkTrue(RetryScopes.hasOuterRetry());
                    throw new IllegalStateException("boom");
                }
            }
        } catch (final IllegalStateException expected) {
            Assertions.checkEquals("boom", expected.getMessage());
        }
        Assertions.checkEquals(0, RetryScopes.currentDepth());
        //no growth across many install/close cycles on a reused thread
        for (int i = 0; i < 1000; i++) {
            try (RetryScopes.Registration scope = RetryScopes.install(2)) {
                Assertions.checkEquals(2, RetryScopes.currentDepth());
            }
        }
        Assertions.checkEquals(0, RetryScopes.currentDepth());
    }

}
