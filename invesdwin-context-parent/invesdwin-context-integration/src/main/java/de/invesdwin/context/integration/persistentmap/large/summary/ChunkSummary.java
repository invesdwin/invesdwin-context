package de.invesdwin.context.integration.persistentmap.large.summary;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.norva.marker.ISerializableValueObject;
import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.marshallers.serde.ISerde;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.streams.buffer.MemoryMappedFile;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.extend.UnsafeByteBuffer;

@Immutable
public class ChunkSummary implements ISerializableValueObject {

    private final String memoryResourceUri;
    private final long memoryOffset;
    private final long memoryLength;
    private final int hashCode;

    public <V> ChunkSummary(final String memoryResourceUri, final long memoryOffset, final long memoryLength) {
        this.memoryResourceUri = memoryResourceUri;
        this.memoryOffset = memoryOffset;
        this.memoryLength = memoryLength;
        this.hashCode = newHashCode();
    }

    public <V> ChunkSummary(final ISerde<V> serde, final String memoryResourceUri, final long memoryOffset,
            final long memoryLength) {
        this.memoryResourceUri = memoryResourceUri;
        this.memoryOffset = memoryOffset;
        this.memoryLength = memoryLength;
        this.hashCode = newHashCode();
    }

    private int newHashCode() {
        return Objects.hashCode(memoryResourceUri, memoryOffset, memoryLength);
    }

    public String getMemoryResourceUri() {
        return memoryResourceUri;
    }

    public long getMemoryOffset() {
        return memoryOffset;
    }

    public long getMemoryLength() {
        return memoryLength;
    }

    public IByteBuffer newBuffer(final MemoryMappedFile file) {
        final long address = file.getAddress() + getMemoryOffset();
        final int length = Integers.checkedCast(getMemoryLength());
        return new UnsafeByteBuffer(address, length);
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof ChunkSummary) {
            final ChunkSummary cObj = (ChunkSummary) obj;
            return Objects.equals(cObj.memoryResourceUri, memoryResourceUri) && cObj.memoryOffset == memoryOffset
                    && cObj.memoryLength == memoryLength;
        }
        return false;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("uri", memoryResourceUri)
                .add("offset", memoryOffset)
                .add("length", memoryLength)
                .toString();
    }
}