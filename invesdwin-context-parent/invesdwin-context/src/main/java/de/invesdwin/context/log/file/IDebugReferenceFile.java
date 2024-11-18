package de.invesdwin.context.log.file;

import de.invesdwin.util.concurrent.reference.integer.IMutableIntReference;

public interface IDebugReferenceFile {

    IMutableIntReference newRequestId();

    void writeLine(String format, Object... args);

    static IDebugReferenceFile newInstance(final Object source, final boolean enabled) {
        if (enabled) {
            return new DebugReferenceFile(source);
        } else {
            return DisabledDebugReferenceFile.INSTANCE;
        }
    }

}
