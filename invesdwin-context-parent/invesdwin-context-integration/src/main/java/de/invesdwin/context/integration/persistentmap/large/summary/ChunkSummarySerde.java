package de.invesdwin.context.integration.persistentmap.large.summary;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.marshallers.serde.ISerde;
import de.invesdwin.util.marshallers.serde.SerdeBaseMethods;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

@NotThreadSafe
public final class ChunkSummarySerde implements ISerde<ChunkSummary> {

    public static final ChunkSummarySerde GET = new ChunkSummarySerde();

    private static final int MEMORYRESOURCEURISIZE_INDEX = 0;
    private static final int MEMORYRESOURCEURISIZE_SIZE = Integer.BYTES;

    private static final int MEMORYOFFSET_INDEX = MEMORYRESOURCEURISIZE_INDEX + MEMORYRESOURCEURISIZE_SIZE;
    private static final int MEMORYOFFSET_SIZE = Long.BYTES;

    private static final int MEMORYLENGTH_INDEX = MEMORYOFFSET_INDEX + MEMORYOFFSET_SIZE;
    private static final int MEMORYLENGTH_SIZE = Long.BYTES;

    private static final int MEMORYRESOURCEURI_INDEX = MEMORYLENGTH_INDEX + MEMORYLENGTH_SIZE;

    private ChunkSummarySerde() {}

    @Override
    public ChunkSummary fromBytes(final byte[] bytes) {
        return SerdeBaseMethods.fromBytes(this, bytes);
    }

    @Override
    public byte[] toBytes(final ChunkSummary obj) {
        return SerdeBaseMethods.toBytes(this, obj);
    }

    @Override
    public ChunkSummary fromBuffer(final IByteBuffer buffer) {
        final int memoryResourceUriSize = buffer.getInt(MEMORYRESOURCEURISIZE_INDEX);
        final long memoryOffset = buffer.getLong(MEMORYOFFSET_INDEX);
        final long memoryLength = buffer.getLong(MEMORYLENGTH_INDEX);
        final String memoryResourceUri = buffer.getStringUtf8(MEMORYRESOURCEURI_INDEX, memoryResourceUriSize);
        return new ChunkSummary(memoryResourceUri, memoryOffset, memoryLength);
    }

    @Override
    public int toBuffer(final IByteBuffer buffer, final ChunkSummary obj) {
        final String memoryResourceUri = obj.getMemoryResourceUri();
        final byte[] memoryResourceUriBytes = ByteBuffers.newStringUtf8Bytes(memoryResourceUri);
        final int memoryResourceUriSize = memoryResourceUriBytes.length;
        final long memoryOffset = obj.getMemoryOffset();
        final long memorySize = obj.getMemoryLength();

        buffer.putInt(MEMORYRESOURCEURISIZE_INDEX, memoryResourceUriSize);
        buffer.putLong(MEMORYOFFSET_INDEX, memoryOffset);
        buffer.putLong(MEMORYLENGTH_INDEX, memorySize);
        int position = MEMORYRESOURCEURI_INDEX;
        buffer.putBytes(position, memoryResourceUriBytes);
        position += memoryResourceUriBytes.length;
        return position;
    }

}
