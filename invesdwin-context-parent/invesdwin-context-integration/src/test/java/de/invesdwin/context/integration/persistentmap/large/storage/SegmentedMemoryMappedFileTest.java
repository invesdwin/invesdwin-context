package de.invesdwin.context.integration.persistentmap.large.storage;

import java.io.File;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import de.invesdwin.context.ContextProperties;
import de.invesdwin.context.test.ATest;
import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.streams.buffer.memory.extend.SegmentedMappedExpandableMemoryBuffer;

@Disabled("manual test")
@NotThreadSafe
public class SegmentedMemoryMappedFileTest extends ATest {

    @Test
    public void test() {
        final File memoryDirectory = ContextProperties.getCacheDirectory();
        final SegmentedMappedExpandableMemoryBuffer buffer = new SegmentedMappedExpandableMemoryBuffer(
                SegmentedMappedExpandableMemoryBuffer.INITIAL_CAPACITY, new File(memoryDirectory, "buffer"));

        long bufferSize = 0;

        for (int i = 10_000_000; i < 100_000_000; i += 100_000) {
            log.info("%s: %s", i, Doubles.divide(bufferSize, 1024 * 1024 * 1024));
            final byte[] value = new byte[i];
            for (int v = 0; v < value.length; v++) {
                value[v] = (byte) v;
            }
            buffer.putBytes(bufferSize, value);

            final byte[] bufferValue = buffer.asByteArrayCopy(bufferSize, value.length);
            Assertions.assertThat(bufferValue).isEqualTo(value);
            bufferSize += value.length;
        }

    }

}
