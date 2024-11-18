package de.invesdwin.context.log.file;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.concurrent.reference.integer.DisabledMMutableIntReference;
import de.invesdwin.util.concurrent.reference.integer.IMutableIntReference;

@Immutable
public final class DisabledDebugReferenceFile implements IDebugReferenceFile {

    public static final DisabledDebugReferenceFile INSTANCE = new DisabledDebugReferenceFile();

    private DisabledDebugReferenceFile() {}

    @Override
    public IMutableIntReference newCounter() {
        return DisabledMMutableIntReference.INSTANCE;
    }

    @Override
    public void writeLine(final String format, final Object... args) {
        //noop
    }

}
