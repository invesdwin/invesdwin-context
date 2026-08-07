package de.invesdwin.context.integration.filechannel.io;

import java.io.File;
import java.net.URI;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.context.integration.filechannel.info.FileChannelInfos;
import de.invesdwin.context.integration.filechannel.info.IFileInfo;
import de.invesdwin.util.time.date.FDate;

@Immutable
public class IoFileInfo implements IFileInfo {

    private final URI serverUri;
    private final URI baseServerUri;
    private final String baseDirectory;
    private final String subDirectory;
    private final File delegate;

    private final boolean isDirectory;
    private final boolean isFile;
    private final long length;
    private final FDate lastModified;

    public IoFileInfo(final URI serverUri, final URI baseServerUri, final String baseDirectory,
            final String subDirectory, final File delegate) {
        this.serverUri = serverUri;
        this.baseServerUri = baseServerUri;
        this.baseDirectory = baseDirectory;
        this.subDirectory = subDirectory;
        this.delegate = delegate;

        this.isDirectory = delegate.isDirectory();
        this.isFile = delegate.isFile();
        this.length = delegate.length();
        this.lastModified = new FDate(delegate.lastModified());
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
    public String getAbsoluteDirectory() {
        return FileChannelInfos.combinePath(baseDirectory, subDirectory);
    }

    @Override
    public String getFilename() {
        return delegate.getName();
    }

    @Override
    public boolean isFile() {
        return isFile;
    }

    @Override
    public boolean isDirectory() {
        return isDirectory;
    }

    @Override
    public FDate lastModified() {
        return lastModified;
    }

    @Override
    public long length() {
        return length;
    }

    @Override
    public File unwrap() {
        return delegate;
    }

    @Override
    public String toString() {
        return FileChannelInfos.toString(this);
    }

    public static IoFileInfo valueOf(final URI serverUri, final URI baseServerUri, final String baseDirectory,
            final String subDirectory, final File file) {
        if (file == null) {
            return null;
        }
        return new IoFileInfo(serverUri, baseServerUri, baseDirectory, subDirectory, file);
    }
}