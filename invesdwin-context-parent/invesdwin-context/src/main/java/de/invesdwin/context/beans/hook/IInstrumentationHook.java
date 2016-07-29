package de.invesdwin.context.beans.hook;

import java.lang.instrument.Instrumentation;

public interface IInstrumentationHook {

    void instrument(Instrumentation instrumentation);

}
