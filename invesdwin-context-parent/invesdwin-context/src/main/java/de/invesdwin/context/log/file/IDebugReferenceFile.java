package de.invesdwin.context.log.file;

public interface IDebugReferenceFile {

    void writeLine(String format, Object... args);

    static IDebugReferenceFile newInstance(final Object source, final boolean enabled) {
        if (enabled) {
            return new DebugReferenceFile(source);
        } else {
            return DisabledDebugReferenceFile.INSTANCE;
        }
    }

}
