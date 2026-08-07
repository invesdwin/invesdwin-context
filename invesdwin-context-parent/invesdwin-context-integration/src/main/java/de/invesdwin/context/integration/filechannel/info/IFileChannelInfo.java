package de.invesdwin.context.integration.filechannel.info;

import java.net.URI;

import de.invesdwin.norva.marker.ISerializableValueObject;
import de.invesdwin.util.time.date.FDate;

public interface IFileChannelInfo extends ISerializableValueObject {

    URI getServerUri();

    /**
     * The clean server URI (scheme + authority, without path or trailing slashes).
     */
    URI getBaseServerUri();

    /**
     * The fixed base path extracted from the connection URI (immutable sandbox root).
     */
    String getBaseDirectory();

    /**
     * The mutable relative path inside the base path.
     */
    String getSubDirectory();

    /**
     * The fully resolved absolute directory (BaseDirectory + SubDirectory).
     */
    String getAbsoluteDirectory();

    default URI getDirectoryUri() {
        return FileChannelInfos.newDirectoryUri(getBaseServerUri(), getAbsoluteDirectory());
    }

    default URI getFileUri() {
        return FileChannelInfos.newFileUri(getBaseServerUri(), getAbsoluteDirectory(), getFilename());
    }

    String getFilename();

    default String getAbsolutePath() {
        return FileChannelInfos.newAbsolutePath(getAbsoluteDirectory(), getFilename());
    }

    FDate lastModified();

    long length();

}