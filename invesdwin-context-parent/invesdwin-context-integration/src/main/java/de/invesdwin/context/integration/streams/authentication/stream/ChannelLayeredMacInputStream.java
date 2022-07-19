package de.invesdwin.context.integration.streams.authentication.stream;

import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.ReadableByteChannel;
import java.security.Key;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.context.integration.streams.authentication.pool.IMac;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;

@NotThreadSafe
public class ChannelLayeredMacInputStream extends LayeredMacInputStream implements ReadableByteChannel {

    private final ReadableByteChannel delegateChannel;

    public ChannelLayeredMacInputStream(final InputStream delegate, final IMac mac, final Key key) {
        super(delegate, mac, key);
        this.delegateChannel = (ReadableByteChannel) delegate;
    }

    @Override
    public boolean isOpen() {
        return delegateChannel.isOpen();
    }

    @Override
    public int read(final java.nio.ByteBuffer dst) throws IOException {
        final int positionBefore = dst.position();
        final int length = delegateChannel.read(dst);
        mac.update(ByteBuffers.slice(dst, positionBefore, length));
        return length;
    }

    public static LayeredMacInputStream maybeWrap(final InputStream delegate, final IMac mac, final Key key) {
        if (delegate instanceof ReadableByteChannel) {
            return new ChannelLayeredMacInputStream(delegate, mac, key);
        } else {
            return new LayeredMacInputStream(delegate, mac, key);
        }
    }

}
