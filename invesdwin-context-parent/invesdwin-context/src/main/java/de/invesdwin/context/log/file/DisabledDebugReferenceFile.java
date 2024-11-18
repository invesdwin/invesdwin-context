package de.invesdwin.context.log.file;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class DisabledDebugReferenceFile implements IDebugReferenceFile {

    public static final DisabledDebugReferenceFile INSTANCE = new DisabledDebugReferenceFile();

    private DisabledDebugReferenceFile() {}

    @Override
    public void writeLine(final String format, final Object... args) {
        //noop
    }

}
