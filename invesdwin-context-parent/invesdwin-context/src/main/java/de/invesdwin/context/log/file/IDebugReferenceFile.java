package de.invesdwin.context.log.file;

import de.invesdwin.util.concurrent.reference.integer.IMutableIntReference;

public interface IDebugReferenceFile {

    IMutableIntReference newCounter();

    void writeLine(String format, Object... args);

    static IDebugReferenceFile newInstance(final boolean enabled, final String id, final Object... sources) {
        if (enabled) {
            return new DebugReferenceFile(id, sources);
        } else {
            return DisabledDebugReferenceFile.INSTANCE;
        }
    }

}
