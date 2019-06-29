package de.invesdwin;

import java.lang.instrument.Instrumentation;

import javax.annotation.concurrent.NotThreadSafe;

import org.github.jamm.MemoryMeter;

import de.invesdwin.instrument.DynamicInstrumentationReflections;
import de.invesdwin.util.assertions.Assertions;

// CHECKSTYLE:OFF
@NotThreadSafe
public class AbstractPerformanceTest {
    //CHECKSTYLE:ON

    static {
        Assertions.assertThat(InstrumentationTestInitializer.INSTANCE).isNotNull();
        final Instrumentation instrumentation = DynamicInstrumentationReflections.getInstrumentation();
        MemoryMeter.premain(null, instrumentation);
    }

    public AbstractPerformanceTest() {}

    public static long measureHeapSize(final Object o) {
        return new MemoryMeter().measureDeep(o);
    }

}
