package de.invesdwin.context.integration.streams.authentication.mac.stream;

import java.io.IOException;
import java.io.OutputStream;
import java.security.Key;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.context.integration.streams.authentication.mac.pool.IMac;
import de.invesdwin.util.streams.ASimpleDelegateOutputStream;

@NotThreadSafe
public class LayeredMacOutputStream extends ASimpleDelegateOutputStream {
    protected final IMac mac;
    private final Key key;

    public LayeredMacOutputStream(final OutputStream delegate, final IMac mac, final Key key) {
        super(delegate);
        this.mac = mac;
        this.key = key;
        init();
    }

    public void init() {
        mac.init(key);
    }

    @Override
    public void write(final int b) throws IOException {
        super.write(b);
        mac.update((byte) b);
    }

    @Override
    public void write(final byte[] b) throws IOException {
        super.write(b);
        mac.update(b);
    }

    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException {
        super.write(b, off, len);
        mac.update(b, off, len);
    }

    public IMac getMac() {
        return mac;
    }

}
