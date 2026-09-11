package de.invesdwin.context.integration.filechannel.io;

import java.io.File;
import java.net.URI;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.context.integration.filechannel.info.IFileInfo;
import de.invesdwin.context.integration.filechannel.info.path.FileChannelPaths;
import de.invesdwin.util.time.date.FDate;

@Immutable
public class IoFileInfo implements IFileInfo {

    private final URI serverUri;
    private final URI baseServerUri;
    private final String baseDirectory;
    private final String subDirectory;
    private final File delegate;

    // Lazy and transient fields
    private transient volatile Boolean isDirectory;
    private transient volatile Boolean isFile;
    private transient volatile Long length;
    private transient volatile FDate lastModified;

    public IoFileInfo(final URI serverUri, final URI baseServerUri, final String baseDirectory,
            final String subDirectory, final File delegate) {
        this.serverUri = serverUri;
        this.baseServerUri = baseServerUri;
        this.baseDirectory = baseDirectory;
        this.subDirectory = subDirectory;
        this.delegate = delegate;
        // Removed eager file system I/O evaluation from the constructor
    }

    @Override
    public URI getServerUri() {
        return serverUri;
    }

    @Override
    public URI getBaseServerUri() {
        return baseServerUri;
    }

    @Override
    public String getBaseDirectory() {
        return baseDirectory;
    }

    @Override
    public String getSubDirectory() {
        return subDirectory;
    }

    @Override
    public String getFileName() {
        return delegate.getName();
    }

    @Override
    public boolean isFile() {
        if (isFile == null) {
            isFile = delegate.isFile();
        }
        return isFile;
    }

    @Override
    public boolean isDirectory() {
        if (isDirectory == null) {
            isDirectory = delegate.isDirectory();
        }
        return isDirectory;
    }

    @Override
    public FDate lastModified() {
        if (lastModified == null) {
            lastModified = new FDate(delegate.lastModified());
        }
        return lastModified;
    }

    @Override
    public long length() {
        if (length == null) {
            length = delegate.length();
        }
        return length;
    }

    @Override
    public File unwrap() {
        return delegate;
    }

    @Override
    public boolean equals(final Object obj) {
        return FileChannelPaths.equals(this, obj);
    }

    @Override
    public int hashCode() {
        return FileChannelPaths.hashCode(this);
    }

    @Override
    public String toString() {
        return FileChannelPaths.toString(this);
    }

    public static IoFileInfo valueOf(final URI serverUri, final URI baseServerUri, final String baseDirectory,
            final String subDirectory, final File file) {
        if (file == null) {
            return null;
        }
        return new IoFileInfo(serverUri, baseServerUri, baseDirectory, subDirectory, file);
    }
}