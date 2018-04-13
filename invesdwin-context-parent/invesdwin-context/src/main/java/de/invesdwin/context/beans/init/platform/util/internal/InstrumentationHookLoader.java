package de.invesdwin.context.beans.init.platform.util.internal;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.context.beans.hook.IInstrumentationHook;
import de.invesdwin.instrument.DynamicInstrumentationReflections;

@NotThreadSafe
public final class InstrumentationHookLoader {

    private InstrumentationHookLoader() {}

    public static void runInstrumentationHooks() {
        final Set<IInstrumentationHook> instrumentationHooks = new HashSet<IInstrumentationHook>();
        for (final IInstrumentationHook hook : ServiceLoader.load(IInstrumentationHook.class)) {
            instrumentationHooks.add(hook);
        }
        for (final IInstrumentationHook instrumentationHook : instrumentationHooks) {
            instrumentationHook.instrument(DynamicInstrumentationReflections.getInstrumentation());
        }
        if (!instrumentationHooks.isEmpty()) {
            final StringBuilder message = new StringBuilder();
            message.append("Loading ");
            message.append(instrumentationHooks.size());
            message.append(" instrumentation hook");
            final List<String> instrumentationHookNames = new ArrayList<String>();
            for (final IInstrumentationHook hook : instrumentationHooks) {
                instrumentationHookNames.add(hook.getClass().getSimpleName());
            }
            if (instrumentationHooks.size() > 1) {
                message.append("s");
            }
            message.append(" ");
            message.append(instrumentationHookNames);
            org.slf4j.ext.XLoggerFactory.getXLogger(InstrumentationHookLoader.class).info(message.toString());
        }
    }

}
