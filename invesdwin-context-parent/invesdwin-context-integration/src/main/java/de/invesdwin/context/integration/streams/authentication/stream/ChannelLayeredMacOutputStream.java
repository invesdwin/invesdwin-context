package de.invesdwin.context.integration.streams.authentication.stream;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.WritableByteChannel;
import java.security.Key;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.context.integration.streams.authentication.pool.IMac;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;

@NotThreadSafe
public class ChannelLayeredMacOutputStream extends LayeredMacOutputStream implements WritableByteChannel {

    private final WritableByteChannel delegateChannel;

    public ChannelLayeredMacOutputStream(final OutputStream delegate, final IMac mac, final Key key) {
        super(delegate, mac, key);
        this.delegateChannel = (WritableByteChannel) delegate;
    }

    @Override
    public boolean isOpen() {
        return delegateChannel.isOpen();
    }

    @Override
    public int write(final java.nio.ByteBuffer src) throws IOException {
        final int positionBefore = src.position();
        final int length = delegateChannel.write(src);
        mac.update(ByteBuffers.slice(src, positionBefore, length));
        return length;
    }

    public static LayeredMacOutputStream maybeWrap(final OutputStream delegate, final IMac mac, final Key key) {
        if (delegate instanceof WritableByteChannel) {
            return new ChannelLayeredMacOutputStream(delegate, mac, key);
        } else {
            return new LayeredMacOutputStream(delegate, mac, key);
        }
    }

}
