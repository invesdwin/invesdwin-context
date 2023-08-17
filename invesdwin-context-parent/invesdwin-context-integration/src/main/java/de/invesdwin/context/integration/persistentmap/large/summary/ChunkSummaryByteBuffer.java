package de.invesdwin.context.integration.persistentmap.large.summary;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Supplier;

import javax.annotation.concurrent.ThreadSafe;

import org.agrona.MutableDirectBuffer;

import de.invesdwin.util.concurrent.lock.ILock;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.delegate.ALockedDelegateByteBuffer;
import de.invesdwin.util.streams.buffer.file.IMemoryMappedFile;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;

@ThreadSafe
public class ChunkSummaryByteBuffer extends ALockedDelegateByteBuffer {

    private final Supplier<IMemoryMappedFile> fileSupplier;
    private final ChunkSummary summary;
    private IMemoryMappedFile file;
    private IByteBuffer buffer;

    public ChunkSummaryByteBuffer(final ChunkSummary summary, final Supplier<IMemoryMappedFile> fileSupplier,
            final ILock fileLock) {
        super(fileLock);
        this.fileSupplier = fileSupplier;
        this.summary = summary;
    }

    @Override
    public IByteBuffer getDelegate() {
        if (this.file == null || this.file.isClosed()) {
            this.file = fileSupplier.get();
            if (this.file == null) {
                return null;
            }
            final int length = Integers.checkedCast(summary.getMemoryLength());
            this.buffer = file.newByteBuffer(summary.getMemoryOffset(), length);
        }
        return this.buffer;
    }

    @Override
    public MutableDirectBuffer asDirectBuffer() {
        throw new UnsupportedOperationException();
    }

    @Override
    public MutableDirectBuffer asDirectBuffer(final int index, final int length) {
        throw new UnsupportedOperationException();
    }

    @Override
    public MutableDirectBuffer asDirectBufferFrom(final int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public MutableDirectBuffer asDirectBufferTo(final int length) {
        throw new UnsupportedOperationException();
    }

    @Override
    public InputStream asInputStream() {
        throw new UnsupportedOperationException();
    }

    @Override
    public InputStream asInputStream(final int index, final int length) {
        throw new UnsupportedOperationException();
    }

    @Override
    public InputStream asInputStreamFrom(final int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public InputStream asInputStreamTo(final int length) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IMemoryBuffer asMemoryBuffer() {
        throw new UnsupportedOperationException();
    }

    @Override
    public IMemoryBuffer asMemoryBuffer(final int index, final int length) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IMemoryBuffer asMemoryBufferFrom(final int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IMemoryBuffer asMemoryBufferTo(final int length) {
        throw new UnsupportedOperationException();
    }

    @Override
    public java.nio.ByteBuffer asNioByteBuffer() {
        throw new UnsupportedOperationException();
    }

    @Override
    public java.nio.ByteBuffer asNioByteBuffer(final int index, final int length) {
        throw new UnsupportedOperationException();
    }

    @Override
    public java.nio.ByteBuffer asNioByteBufferFrom(final int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public java.nio.ByteBuffer asNioByteBufferTo(final int length) {
        throw new UnsupportedOperationException();
    }

    @Override
    public OutputStream asOutputStream() {
        throw new UnsupportedOperationException();
    }

    @Override
    public OutputStream asOutputStream(final int index, final int length) {
        throw new UnsupportedOperationException();
    }

    @Override
    public OutputStream asOutputStreamFrom(final int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public OutputStream asOutputStreamTo(final int length) {
        throw new UnsupportedOperationException();
    }

    @Override
    public java.nio.ByteBuffer nioByteBuffer() {
        throw new UnsupportedOperationException();
    }

    @Override
    public MutableDirectBuffer directBuffer() {
        throw new UnsupportedOperationException();
    }

}
