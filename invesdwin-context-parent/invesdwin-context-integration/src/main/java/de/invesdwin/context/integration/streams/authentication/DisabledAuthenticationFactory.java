package de.invesdwin.context.integration.streams.authentication;

import java.io.InputStream;
import java.io.OutputStream;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.context.integration.streams.authentication.pool.DisabledMac;
import de.invesdwin.context.integration.streams.authentication.stream.LayeredMacInputStream;
import de.invesdwin.context.integration.streams.authentication.stream.LayeredMacOutputStream;
import de.invesdwin.util.marshallers.serde.ISerde;
import de.invesdwin.util.math.Bytes;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;
import de.invesdwin.util.streams.pool.buffered.PooledFastBufferedOutputStream;

@Immutable
public final class DisabledAuthenticationFactory implements IAuthenticationFactory {

    public static final DisabledAuthenticationFactory INSTANCE = new DisabledAuthenticationFactory();

    private DisabledAuthenticationFactory() {
    }

    @Override
    public LayeredMacOutputStream newMacOutputStream(final OutputStream out) {
        //buffering is better for write throughput to file
        return new LayeredMacOutputStream(PooledFastBufferedOutputStream.newInstance(out), DisabledMac.INSTANCE, null);
    }

    @Override
    public LayeredMacInputStream newMacInputStream(final InputStream in) {
        return new LayeredMacInputStream(in, DisabledMac.INSTANCE, null);
    }

    @Override
    public byte[] mac(final IByteBuffer src) {
        return Bytes.EMPTY_ARRAY;
    }

    @Override
    public int sign(final IByteBuffer src, final IByteBuffer dest) {
        dest.putBytes(0, src);
        return src.capacity();
    }

    @Override
    public int verify(final IByteBuffer src, final IByteBuffer dest) {
        dest.putBytes(0, src);
        return src.capacity();
    }

    @Override
    public <T> ISerde<T> maybeWrap(final ISerde<T> serde) {
        return serde;
    }

}
