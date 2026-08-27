package de.invesdwin.context.integration.filechannel.io;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.context.integration.filechannel.IFileChannel;
import de.invesdwin.context.integration.filechannel.info.path.IFileChannelPath;
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
    public IFileChannel newInstance(final IFileChannelPath path) {
        //CHECKSTYLE:OFF
        return new IoFileChannel(path);
        //CHECKSTYLE:ON
    }

}