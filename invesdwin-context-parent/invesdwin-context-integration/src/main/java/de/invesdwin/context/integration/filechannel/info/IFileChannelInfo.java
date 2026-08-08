package de.invesdwin.context.integration.filechannel.info;

import java.net.URI;

import de.invesdwin.norva.marker.ISerializableValueObject;
import de.invesdwin.util.lang.string.Strings;
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

    default String getDirectoryName() {
        final String absDir = getAbsoluteDirectory();
        if (Strings.isBlank(absDir) || "/".equals(absDir)) {
            return "";
        }
        final String clean = Strings.removeEnd(absDir, "/");
        final int lastSlash = clean.lastIndexOf('/');
        if (lastSlash == -1) {
            return clean;
        }
        return clean.substring(lastSlash + 1);
    }

    default String getParentDirectory() {
        final String absDir = getAbsoluteDirectory();
        if (Strings.isBlank(absDir) || "/".equals(absDir)) {
            return null;
        }
        final String clean = Strings.removeEnd(absDir, "/");
        final int lastSlash = clean.lastIndexOf('/');
        if (lastSlash == -1) {
            return "/";
        }
        return clean.substring(0, lastSlash + 1);
    }

    default String getParentPath() {
        try {
            final String filename = getFilename();
            if (Strings.isNotBlank(filename)) {
                return getAbsoluteDirectory();
            }
        } catch (final Exception e) {
            // filename not set
        }
        return getParentDirectory();
    }

    default URI getDirectoryUri() {
        return FileChannelInfos.newDirectoryUri(getBaseServerUri(), getAbsoluteDirectory());
    }

    default URI getParentDirectoryUri() {
        final String parentDir = getParentDirectory();
        if (parentDir == null) {
            return getBaseServerUri();
        }
        return FileChannelInfos.newDirectoryUri(getBaseServerUri(), parentDir);
    }

    default URI getParentUri() {
        final String parent = getParentPath();
        if (parent == null) {
            return getBaseServerUri();
        }
        return FileChannelInfos.newDirectoryUri(getBaseServerUri(), parent);
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