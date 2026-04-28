package de.invesdwin.context.integration.persistentmap.large.summary;

import java.io.Closeable;
import java.io.File;

import javax.annotation.concurrent.ThreadSafe;

import org.agrona.MutableDirectBuffer;

import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.delegate.MemoryDelegateByteBuffer;
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
        final long length = summary.getMemoryLength();
        this.buffer = file.newMemoryBuffer(summary.getPrecedingMemoryOffset() + summary.getMemoryOffset(), length);
        this.file = file;
    }

    public void init(final File file, final IMemoryBuffer buffer) {
        final long length = summary.getMemoryLength();
        this.buffer = buffer.newSlice(summary.getPrecedingMemoryOffset() + summary.getMemoryOffset(), length);
    }

    @Override
    public MutableDirectBuffer asDirectBuffer(final long index, final int length) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IByteBuffer asByteBuffer(final long index, final int length) {
        return new MemoryDelegateByteBuffer(newSlice(index, length));
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
