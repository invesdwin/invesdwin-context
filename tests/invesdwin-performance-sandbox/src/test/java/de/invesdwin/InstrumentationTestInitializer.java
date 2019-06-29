package de.invesdwin;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.instrument.DynamicInstrumentationLoader;

@Immutable
public final class InstrumentationTestInitializer {

    public static final InstrumentationTestInitializer INSTANCE = new InstrumentationTestInitializer();

    static {
        //initialize load time weaving
        DynamicInstrumentationLoader.waitForInitialized();
        org.assertj.core.api.Assertions.assertThat(DynamicInstrumentationLoader.initLoadTimeWeavingContext())
                .isNotNull();
    }

    private InstrumentationTestInitializer() {}

}
