package de.invesdwin.context.test;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.attributes.AttributesMap;
import de.invesdwin.util.collections.attributes.IAttributesMap;
import de.invesdwin.util.time.date.FTimeUnit;

@Immutable
class TestContextState {

    private final Set<ATest> registeredTests = new LinkedHashSet<ATest>();
    private final AtomicInteger registeredTestsCount = new AtomicInteger();
    private final List<String> locationStrings;
    private volatile TestContext context;
    private final IAttributesMap attributes = new AttributesMap();

    TestContextState(final List<String> locationStrings) {
        this.locationStrings = locationStrings;
    }

    List<String> getLocationStrings() {
        return locationStrings;
    }

    synchronized void registerTest(final ATest test) {
        if (registeredTests.add(test)) {
            registeredTestsCount.incrementAndGet();
        }
    }

    synchronized void unregisterTest(final ATest test) {
        if (registeredTests.remove(test)) {
            registeredTestsCount.decrementAndGet();
        }
    }

    void setContext(final TestContext context) {
        this.context = context;
    }

    TestContext getContext() {
        return context;
    }

    void waitForFinished() {
        while (!isFinished()) {
            FTimeUnit.MILLISECONDS.sleepNoInterrupt(1);
        }
    }

    boolean isFinished() {
        return registeredTestsCount.get() <= 0;
    }

    IAttributesMap getAttributes() {
        return attributes;
    }

}
