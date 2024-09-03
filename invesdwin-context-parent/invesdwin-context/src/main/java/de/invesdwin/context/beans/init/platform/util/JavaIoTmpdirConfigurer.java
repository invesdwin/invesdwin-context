package de.invesdwin.context.beans.init.platform.util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.context.log.Log;
import de.invesdwin.instrument.DynamicInstrumentationReflections;
import de.invesdwin.util.lang.Files;

@Immutable
public final class JavaIoTmpdirConfigurer {

    private JavaIoTmpdirConfigurer() {}

    public static File configure(final File tempDirectory) {
        final File javaIoTmpdir = new File(tempDirectory, "java.io.tmpdir");
        try {
            Files.forceMkdir(javaIoTmpdir);
            //CHECKSTYLE:OFF
            System.setProperty("java.io.tmpdir", javaIoTmpdir.getAbsolutePath());
            //CHECKSTYLE:ON
            configureStaticProperty(javaIoTmpdir);
            configureTempFileHelper(javaIoTmpdir);
            final File tmpFile = Files.createTempFile("test", "test").toFile();
            Files.deleteQuietly(tmpFile);
            if (!tmpFile.getParentFile().equals(javaIoTmpdir)) {
                Files.deleteQuietly(javaIoTmpdir);
                final Log log = new Log(JavaIoTmpdirConfigurer.class);
                log.warn("Unable to change java.io.tmpdir in %s from %s to %s", java.nio.file.Files.class.getName(),
                        tmpFile.getParentFile(), javaIoTmpdir);

            }
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        return javaIoTmpdir;
    }

    private static void configureTempFileHelper(final File javaIoTmpdir) {
        //Path java.nio.file.TempFileHelper.tmpdir
        final String tempFileHelper = "java.nio.file.TempFileHelper";
        try {
            final Class<?> tempFileHelperClass = Class.forName(tempFileHelper);
            final Field tmpdirField = tempFileHelperClass.getDeclaredField("tmpdir");
            tmpdirField.setAccessible(true);
            DynamicInstrumentationReflections.removeFinalModifier(tmpdirField);
            tmpdirField.set(null, javaIoTmpdir.toPath());
        } catch (final NoSuchFieldException | ClassNotFoundException e) {
            //ignore, might be a different java version
        } catch (final SecurityException e) {
            throw new RuntimeException(e);
        } catch (final IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static void configureStaticProperty(final File javaIoTmpdir) {
        //String jdk.internal.util.StaticProperty.JAVA_IO_TMPDIR
        final String staticProperty = "jdk.internal.util.StaticProperty";
        try {
            final Class<?> staticPropertyClass = Class.forName(staticProperty);
            final Field javaIoTmpdirField = staticPropertyClass.getDeclaredField("JAVA_IO_TMPDIR");
            javaIoTmpdirField.setAccessible(true);
            DynamicInstrumentationReflections.removeFinalModifier(javaIoTmpdirField);
            javaIoTmpdirField.set(null, javaIoTmpdir.getAbsolutePath());
        } catch (final NoSuchFieldException | ClassNotFoundException e) {
            //ignore, might be a different java version
        } catch (final SecurityException e) {
            throw new RuntimeException(e);
        } catch (final IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
