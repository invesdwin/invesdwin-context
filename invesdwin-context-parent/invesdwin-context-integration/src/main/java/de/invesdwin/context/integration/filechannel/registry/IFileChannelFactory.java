package de.invesdwin.context.integration.filechannel.registry;

import de.invesdwin.context.integration.filechannel.IFileChannel;
import de.invesdwin.context.integration.filechannel.info.path.IFileChannelPath;

public interface IFileChannelFactory {

    /**
     * Returns the URI scheme/protocol supported by this factory (e.g., "file", "webdav", "hdfs").
     */
    String getScheme();

    /**
     * Creates an instance of IFileChannel configured for the given IFileChannelPath.
     */
    IFileChannel newInstance(IFileChannelPath path);

}