package de.invesdwin.context.test;

import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.attributes.AttributesMap;
import de.invesdwin.util.collections.attributes.IAttributesMap;
import de.invesdwin.util.collections.factory.ILockCollectionFactory;
import de.invesdwin.util.time.date.FTimeUnit;

@ThreadSafe
class TestContextState {

    static final AtomicInteger ACTIVE_COUNT_GLOBAL = new AtomicInteger();

    private final Set<Class<? extends ATest>> registeredTests = ILockCollectionFactory.getInstance(false)
            .newIdentitySet();
    private final AtomicInteger registeredTestsCount = new AtomicInteger();
    private final List<String> locationStrings;
    private final Set<String> contextModifications = ILockCollectionFactory.getInstance(false).newLinkedSet();
    private volatile TestContext context;
    private final IAttributesMap attributes = new AttributesMap();

    TestContextState(final List<String> locationStrings) {
        this.locationStrings = locationStrings;
    }

    List<String> getLocationStrings() {
        return locationStrings;
    }

    Set<String> getContextModifications() {
        return contextModifications;
    }

    synchronized void registerTest(final ATest test) {
        if (registeredTests.add(test.getClass())) {
            registeredTestsCount.incrementAndGet();
        }
    }

    synchronized void unregisterTest(final ATest test) {
        if (registeredTests.remove(test.getClass())) {
            registeredTestsCount.decrementAndGet();
        }
    }

    void setContext(final TestContext context) {
        this.context = context;
    }

    TestContext getContext() {
        return context;
    }

    void waitForFinishedContext() {
        while (!isFinishedContext()) {
            FTimeUnit.MILLISECONDS.sleepNoInterrupt(1);
        }
    }

    boolean isFinishedContext() {
        return registeredTestsCount.get() <= 1;
    }

    static boolean isFinishedGlobal() {
        return ACTIVE_COUNT_GLOBAL.get() <= 0;
    }

    IAttributesMap getAttributes() {
        return attributes;
    }

}
