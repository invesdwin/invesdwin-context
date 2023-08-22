package de.invesdwin.context.system.array;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.context.test.ATest;
import de.invesdwin.context.test.stub.StubSupport;
import jakarta.inject.Named;

@Immutable
@Named
public class PrimitiveArrayAllocatorStub extends StubSupport {

    @Override
    public void tearDownOnce(final ATest test) {
        OnHeapPrimitiveArrayAllocator.INSTANCE.clear();
        OffHeapPrimitiveArrayAllocator.INSTANCE.clear();
    }

}
