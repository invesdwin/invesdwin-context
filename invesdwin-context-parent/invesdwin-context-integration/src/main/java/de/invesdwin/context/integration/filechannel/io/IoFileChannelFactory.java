package de.invesdwin.context.integration.filechannel.io;

import java.net.URI;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.context.integration.filechannel.IFileChannel;
import de.invesdwin.context.integration.filechannel.registry.IFileChannelFactory;

@Immutable
public class IoFileChannelFactory implements IFileChannelFactory {
    private final String scheme;

    public IoFileChannelFactory(final String scheme) {
        this.scheme = scheme;
    }

    @Override
    public String getScheme() {
        return scheme;
    }

    @Override
    public IFileChannel newInstance(final URI serverUri) {
        //CHECKSTYLE:OFF
        return new IoFileChannel(serverUri);
        //CHECKSTYLE:ON
    }
}