package de.invesdwin.context.integration.filechannel.registry;

import java.net.URI;

import de.invesdwin.context.integration.filechannel.IFileChannel;

public interface IFileChannelFactory {

    /**
     * Returns the URI scheme/protocol supported by this factory (e.g., "file", "webdav", "hdfs").
     */
    String getScheme();

    /**
     * Creates an instance of IFileChannel configured for the given server URI.
     */
    IFileChannel newInstance(URI serverUri);

}