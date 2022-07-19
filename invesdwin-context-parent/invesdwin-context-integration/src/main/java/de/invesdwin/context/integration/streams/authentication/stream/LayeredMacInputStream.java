package de.invesdwin.context.integration.streams.authentication.stream;

import java.io.IOException;
import java.io.InputStream;
import java.security.Key;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.context.integration.streams.authentication.pool.IMac;
import de.invesdwin.util.streams.ASimpleDelegateInputStream;

@NotThreadSafe
public class LayeredMacInputStream extends ASimpleDelegateInputStream {
    protected final IMac mac;
    private final Key key;

    public LayeredMacInputStream(final InputStream delegate, final IMac mac, final Key key) {
        super(delegate);
        this.mac = mac;
        this.key = key;
        init();
    }

    public void init() {
        mac.init(key);
    }

    @Override
    public int read() throws IOException {
        final int b = super.read();
        if (b > 0) {
            mac.update((byte) b);
        }
        return b;
    }

    @Override
    public int read(final byte[] b) throws IOException {
        final int n = super.read(b);
        if (n > 0) {
            mac.update(b, 0, n);
        }
        return n;
    }

    @Override
    public byte[] readAllBytes() throws IOException {
        final byte[] bytes = super.readAllBytes();
        if (bytes.length > 0) {
            mac.update(bytes);
        }
        return bytes;
    }

    @Override
    public int readNBytes(final byte[] b, final int off, final int len) throws IOException {
        final int n = super.readNBytes(b, off, len);
        if (n > 0) {
            mac.update(b, off, n);
        }
        return n;
    }

    @Override
    public byte[] readNBytes(final int len) throws IOException {
        final byte[] bytes = super.readNBytes(len);
        if (bytes.length > 0) {
            mac.update(bytes);
        }
        return bytes;
    }

    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        final int n = super.read(b, off, len);
        if (n > 0) {
            mac.update(b, off, n);
        }
        return n;
    }

    public IMac getMac() {
        return mac;
    }

}
