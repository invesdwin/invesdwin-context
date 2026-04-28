package de.invesdwin.context.integration.persistentmap.large.storage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import de.invesdwin.context.ContextProperties;
import de.invesdwin.context.integration.persistentmap.large.summary.ChunkSummary;
import de.invesdwin.context.test.ATest;
import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.marshallers.serde.ISerde;
import de.invesdwin.util.marshallers.serde.basic.ByteSerde;

@Disabled("manual test")
@NotThreadSafe
public class SegmentedMappedFileChunkStorageTest extends ATest {

    @Test
    public void test() {
        final File memoryDirectory = ContextProperties.getCacheDirectory();
        final ISerde<byte[]> valueSerde = ByteSerde.GET;
        final IChunkStorage<byte[]> list = new ListMappedFileChunkStorage<>(new File(memoryDirectory, "list"),
                valueSerde, false, true);
        final IChunkStorage<byte[]> segment = new SegmentedMappedFileChunkStorage<>(
                new File(memoryDirectory, "segment"), valueSerde, false, true);
        final IChunkStorage<byte[]> largeSegment = new LargeSegmentedMappedFileChunkStorage<>(
                new File(memoryDirectory, "largesegment"), valueSerde, false, true);

        final List<ChunkSummary> listSummaries = new ArrayList<>();
        final List<ChunkSummary> segmentSummaries = new ArrayList<>();
        final List<ChunkSummary> largeSegmentSummaries = new ArrayList<>();

        long listSize = 0;
        long segmentSize = 0;
        long largeSegmentSize = 0;

        for (int i = 1; i < 1_000_000; i++) {
            log.info("%s: %s - %s - %s", i, listSize, segmentSize, largeSegmentSize);
            final byte[] value = new byte[i];
            for (int v = 0; v < value.length; v++) {
                value[v] = (byte) v;
            }
            final ChunkSummary listSummary = list.put(value);
            final ChunkSummary segmentSummary = segment.put(value);
            final ChunkSummary largeSegmentSummary = largeSegment.put(value);
            listSummaries.add(listSummary);
            segmentSummaries.add(segmentSummary);
            largeSegmentSummaries.add(largeSegmentSummary);

            listSize += listSummary.getMemoryLength();
            segmentSize += segmentSummary.getMemoryLength();
            largeSegmentSize += largeSegmentSummary.getMemoryLength();

            Assertions.assertThat(listSummary.getMemoryLength()).isEqualTo(segmentSummary.getMemoryLength());
            Assertions.assertThat(listSummary.getMemoryLength()).isEqualTo(largeSegmentSummary.getMemoryLength());

            final byte[] listValue = list.get(listSummary);
            final byte[] segmentValue = segment.get(segmentSummary);
            final byte[] largeSegmentValue = largeSegment.get(largeSegmentSummary);
            Assertions.assertThat(listValue).isEqualTo(value);
            Assertions.assertThat(segmentValue).isEqualTo(value);
            Assertions.assertThat(largeSegmentValue).isEqualTo(value);
        }

    }

}
