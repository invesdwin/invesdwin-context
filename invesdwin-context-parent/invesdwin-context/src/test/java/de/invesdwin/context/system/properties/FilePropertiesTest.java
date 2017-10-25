package de.invesdwin.context.system.properties;

import java.io.File;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.Test;

import de.invesdwin.context.ContextProperties;
import de.invesdwin.context.test.ATest;
import de.invesdwin.util.assertions.Assertions;

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
    public void testNotThrowsExceptionOnMissing() {
        final File file = new File(ContextProperties.TEMP_DIRECTORY, getClass().getSimpleName());
        final FileProperties newProps = new FileProperties(file);
        Assertions.checkNull(newProps.getBoolean("exists2"));
    }

}
