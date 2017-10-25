package de.invesdwin.context.system.properties;

import java.io.File;
import java.util.NoSuchElementException;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.Test;

import de.invesdwin.context.ContextProperties;
import de.invesdwin.context.test.ATest;
import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.assertions.Executable;

@NotThreadSafe
public class FilePropertiesTest extends ATest {

    @Test
    public void testCreateIfMissing() {
        final File file = new File(ContextProperties.TEMP_DIRECTORY, getClass().getSimpleName());
        final FileProperties props = new FileProperties(file);
        props.setBoolean("exists", true);
        final FileProperties newProps = new FileProperties(file);
        Assertions.checkTrue(newProps.getBoolean("exists"));
    }

    @Test
    public void testThrowsExceptionOnMissing() {
        final File file = new File(ContextProperties.TEMP_DIRECTORY, getClass().getSimpleName());
        final FileProperties newProps = new FileProperties(file);
        Assertions.assertThrows(NoSuchElementException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Assertions.checkTrue(newProps.getBoolean("exists2"));
            }
        });
    }

}
