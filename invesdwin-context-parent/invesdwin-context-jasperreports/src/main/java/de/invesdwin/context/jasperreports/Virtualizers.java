package de.invesdwin.context.jasperreports;

import java.io.File;
import java.io.IOException;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.context.ContextProperties;
import de.invesdwin.context.log.error.Err;
import de.invesdwin.util.lang.Files;
import de.invesdwin.util.lang.string.UniqueNameGenerator;
import net.sf.jasperreports.engine.JRVirtualizer;
import net.sf.jasperreports.engine.fill.JRFileVirtualizer;
import net.sf.jasperreports.engine.fill.JRSwapFileVirtualizer;
import net.sf.jasperreports.engine.util.JRSwapFile;

@Immutable
public final class Virtualizers {

    private static final UniqueNameGenerator UNIQUE_NAME_GENERATOR = new UniqueNameGenerator();

    private Virtualizers() {}

    private static File getTempFolder() {
        final File tempFolder = new File(ContextProperties.TEMP_DIRECTORY, Virtualizers.class.getSimpleName());
        try {
            Files.forceMkdir(tempFolder);
        } catch (final IOException e) {
            throw Err.process(e);
        }
        return tempFolder;
    }

    public static JRVirtualizer newSwapFileVirtualizer(final String name) {
        return new JRSwapFileVirtualizer(2, new JRSwapFile(getTempFolder().getAbsolutePath(), 1024, 1024));
    }

    /**
     * WARNING: Slower than SwapFileVirtualizer!
     */
    public static JRVirtualizer newFileVirtualizer(final String name) {
        final File dir = new File(getTempFolder(), UNIQUE_NAME_GENERATOR.get(name));
        try {
            Files.forceMkdir(dir);
        } catch (final IOException e) {
            throw Err.process(e);
        }
        return new JRFileVirtualizer(2, dir.getAbsolutePath());
    }

}
