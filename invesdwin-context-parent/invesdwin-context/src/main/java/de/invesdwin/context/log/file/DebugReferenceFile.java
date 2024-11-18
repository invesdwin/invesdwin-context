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
import de.invesdwin.util.lang.string.Strings;
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
    private final Object[] sources;
    private final int[] sourcesIdentityhashCode;
    private final String id;
    private final String toString;

    public DebugReferenceFile(final String id, final Object... sources) {
        this.file = new File(BASE_FOLDER, UNIQUE_NAME_GENERATOR.get(id + ".txt"));
        try {
            Files.forceMkdir(file.getParentFile());
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        Files.deleteQuietly(file);

        final Exception initExc = new Exception();
        initExc.fillInStackTrace();
        this.sources = sources;
        this.sourcesIdentityhashCode = new int[sources.length];
        for (int i = 0; i < sources.length; i++) {
            sourcesIdentityhashCode[i] = System.identityHashCode(sources[i]);
        }
        this.id = id;
        this.toString = newToString();
        writeLine("init %s\n%s", this, Throwables.getFullStackTrace(initExc));
    }

    private String newToString() {
        final StringBuilder sb = new StringBuilder(id);
        for (int i = 0; i < sources.length; i++) {
            sb.append(" ");
            final Object source = sources[i];
            if (source == null) {
                sb.append(Strings.NULL_TEXT);
            } else {
                sb.append(source.getClass().getSimpleName());
            }
            sb.append("[");
            sb.append(sourcesIdentityhashCode[i]);
            sb.append(":");
            if (source == null) {
                sb.append(Strings.NULL_TEXT);
            } else {
                sb.append(source.toString());
            }
            sb.append("]");
        }
        return sb.toString();
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
