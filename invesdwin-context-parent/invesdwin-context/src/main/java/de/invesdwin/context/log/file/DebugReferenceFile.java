package de.invesdwin.context.log.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.context.ContextProperties;
import de.invesdwin.util.lang.Files;
import de.invesdwin.util.lang.string.description.TextDescription;

@ThreadSafe
public class DebugReferenceFile implements IDebugReferenceFile {

    private final File file;

    public DebugReferenceFile(final Object source) {
        this.file = new File(ContextProperties.getCacheDirectory(), DebugReferenceFile.class.getSimpleName() + "/"
                + source.getClass().getSimpleName() + "/" + Files.normalizeFilename(source.toString()) + ".trace");
        try {
            Files.forceMkdir(file.getParentFile());
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        Files.deleteQuietly(file);
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

}
