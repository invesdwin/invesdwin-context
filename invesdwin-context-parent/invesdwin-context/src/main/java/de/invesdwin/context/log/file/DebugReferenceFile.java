package de.invesdwin.context.log.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.context.ContextProperties;
import de.invesdwin.util.concurrent.reference.integer.AtomicIntReference;
import de.invesdwin.util.concurrent.reference.integer.IMutableIntReference;
import de.invesdwin.util.error.Throwables;
import de.invesdwin.util.lang.Files;
import de.invesdwin.util.lang.string.UniqueNameGenerator;
import de.invesdwin.util.lang.string.description.TextDescription;

@ThreadSafe
public class DebugReferenceFile implements IDebugReferenceFile {

    private static final File BASE_FOLDER;
    private static final UniqueNameGenerator UNIQUE_NAME_GENERATOR = new UniqueNameGenerator();

    static {
        BASE_FOLDER = new File(ContextProperties.getCacheDirectory(), DebugReferenceFile.class.getSimpleName());
        if (ContextProperties.IS_TEST_ENVIRONMENT) {
            Files.deleteNative(BASE_FOLDER);
        }
    }

    private final File file;
    private final Object source;
    private final int sourceIdentityhashCode;
    private final String id;
    private final String toString;

    public DebugReferenceFile(final Object source, final String id) {
        this.file = new File(BASE_FOLDER, UNIQUE_NAME_GENERATOR.get(id + ".txt"));
        try {
            Files.forceMkdir(file.getParentFile());
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        Files.deleteQuietly(file);

        final Exception initExc = new Exception();
        initExc.fillInStackTrace();
        this.source = source;
        this.sourceIdentityhashCode = System.identityHashCode(source);
        this.id = id;
        this.toString = source.getClass().getSimpleName() + "[" + sourceIdentityhashCode + "] " + id;
        writeLine("init %s\n%s", this, Throwables.getFullStackTrace(initExc));
    }

    @Override
    public IMutableIntReference newCounter() {
        return new AtomicIntReference();
    }

    @Override
    public synchronized void writeLine(final String format, final Object... args) {
        try (FileOutputStream fos = new FileOutputStream(file, true)) {
            final String formatted = TextDescription.format(format, args);
            fos.write(formatted.getBytes());
            fos.write("\n".getBytes());
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return toString;
    }

}
