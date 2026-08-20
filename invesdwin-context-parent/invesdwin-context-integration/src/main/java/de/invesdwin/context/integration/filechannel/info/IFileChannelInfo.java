package de.invesdwin.context.integration.filechannel.info;

import de.invesdwin.context.integration.filechannel.info.path.FileChannelPaths;
import de.invesdwin.context.integration.filechannel.info.path.IFileChannelPath;
import de.invesdwin.util.time.date.FDate;

public interface IFileChannelInfo extends IFileChannelPath {

    /**
     * The fixed base path extracted from the connection URI (immutable sandbox root).
     */
    String getBaseDirectory();

    /**
     * The mutable relative path inside the base path.
     */
    String getSubDirectory();

    @Override
    default String getAbsoluteDirectory() {
        return FileChannelPaths.combinePath(getBaseDirectory(), getSubDirectory());
    }

    FDate lastModified();

    long length();

}