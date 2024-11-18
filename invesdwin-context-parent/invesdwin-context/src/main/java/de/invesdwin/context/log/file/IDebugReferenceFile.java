package de.invesdwin.context.log.file;

import de.invesdwin.util.concurrent.reference.integer.IMutableIntReference;

public interface IDebugReferenceFile {

    IMutableIntReference newRequestId();

    void writeLine(String format, Object... args);

    static IDebugReferenceFile newInstance(final Object source, final String id, final boolean enabled) {
        if (enabled) {
            return new DebugReferenceFile(source, id);
        } else {
            return DisabledDebugReferenceFile.INSTANCE;
        }
    }

}
