package de.invesdwin.context.beans.init.platform.util.internal.protocols.p;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.lang.uri.Addresses;

/**
 * A fake handler for the registry service that reports to be downloadable if the given host:port is reachable.
 */
@Immutable
public class Handler extends URLStreamHandler {

    @Override
    protected URLConnection openConnection(final URL u) throws IOException {
        return new URLConnection(u) {
            @Override
            public void connect() throws IOException {
                if (Addresses.isConnectionPossible(u.getHost(), u.getPort())) {
                    connected = true;
                }
            }

            @Override
            public InputStream getInputStream() throws IOException {
                if (connected) {
                    return new InputStream() {
                        @Override
                        public int read() throws IOException {
                            return -1;
                        }

                        @Override
                        public int available() throws IOException {
                            return 1;
                        }
                    };
                } else {
                    return new InputStream() {
                        @Override
                        public int read() throws IOException {
                            return -1;
                        }

                        @Override
                        public int available() throws IOException {
                            return 0;
                        }
                    };
                }
            }
        };
    }
}
