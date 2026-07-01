package de.invesdwin.context.system.array.base;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.concurrent.nested.DisabledNestedExecutor;
import de.invesdwin.util.concurrent.nested.INestedExecutor;

@Immutable
public final class ArrayAllocators {

    private ArrayAllocators() {}

    public static INestedExecutor newDefaultExecutor(final String name) {
        return DisabledNestedExecutor.INSTANCE;
        //        return new ANestedExecutor(name) {
        //            @Override
        //            protected WrappedExecutorService newNestedExecutor(final String nestedName) {
        //                return Executors.newFixedCallerRunsThreadPool(nestedName, Executors.getCpuThreadPoolCount());
        //            }
        //        };
    }

}
