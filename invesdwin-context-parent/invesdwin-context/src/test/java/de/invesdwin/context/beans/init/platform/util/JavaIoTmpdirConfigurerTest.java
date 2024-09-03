package de.invesdwin.context.beans.init.platform.util;

import java.io.File;
import java.io.IOException;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.Test;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.lang.Files;
import de.invesdwin.util.lang.reflection.Reflections;

@NotThreadSafe
public class JavaIoTmpdirConfigurerTest {

    static {
        Reflections.disableJavaModuleSystemRestrictions();
    }

    @Test
    public void testRedirect() {
        //CHECKSTYLE:OFF
        final File original = new File(System.getProperty("java.io.tmpdir"));
        //CHECKSTYLE:ON
        try {
            final File redirected = JavaIoTmpdirConfigurer.configure(original);
            //CHECKSTYLE:OFF
            Assertions.assertThat(System.getProperty("java.io.tmpdir")).isEqualTo(redirected.getAbsolutePath());
            //CHECKSTYLE:ON
            try {
                final File tempFile = Files.createTempFile("test", "test").toFile();
                Files.deleteQuietly(tempFile);
                Assertions.assertThat(tempFile.getParentFile()).isEqualTo(redirected);
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        } finally {
            //CHECKSTYLE:OFF
            System.setProperty("java.io.tmpdir", original.getAbsolutePath());
            //CHECKSTYLE:ON
        }
    }
}
