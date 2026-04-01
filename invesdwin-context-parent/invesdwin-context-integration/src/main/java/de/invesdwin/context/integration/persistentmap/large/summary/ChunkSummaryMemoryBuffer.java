package de.invesdwin.context.integration.persistentmap.large.summary;

import java.io.Closeable;

import javax.annotation.concurrent.ThreadSafe;

import org.agrona.MutableDirectBuffer;

import de.invesdwin.util.math.Integers;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;
import de.invesdwin.util.streams.buffer.file.IMemoryMappedFile;
import de.invesdwin.util.streams.buffer.memory.ClosedMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.delegate.ADelegateMemoryBuffer;

@ThreadSafe
public class ChunkSummaryMemoryBuffer extends ADelegateMemoryBuffer implements Closeable {

    private final ChunkSummary summary;
    //keep reference to prevent finalizer from running too early
    @SuppressWarnings("unused")
    private volatile IMemoryMappedFile file;
    private volatile IMemoryBuffer buffer;

    public ChunkSummaryMemoryBuffer(final ChunkSummary summary) {
        this.summary = summary;
    }

    @Override
    public IMemoryBuffer getDelegate() {
        return this.buffer;
    }

    public void init(final IMemoryMappedFile file) {
        final long length = Integers.checkedCast(summary.getMemoryLength());
        this.buffer = file.newMemoryBuffer(summary.getPrecedingMemoryOffset() + summary.getMemoryOffset(), length);
        this.file = file;
    }

    @Override
    public MutableDirectBuffer asDirectBuffer(final long index, final int length) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IByteBuffer asByteBuffer(final long index, final int length) {
        throw new UnsupportedOperationException();
    }

    @Override
    public java.nio.ByteBuffer asNioByteBuffer(final long index, final int length) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close() {
        this.buffer = ClosedMemoryBuffer.INSTANCE;
        this.file = null;
    }

}
