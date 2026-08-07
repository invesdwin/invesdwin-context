package de.invesdwin.context.integration.filechannel.io;

import java.io.File;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.context.integration.filechannel.info.FileChannelInfos;
import de.invesdwin.context.integration.filechannel.info.IFileInfo;
import de.invesdwin.util.time.date.FDate;

@Immutable
public class IoFileInfo implements IFileInfo {

    private final String serverUri;
    private final String directory;
    private final File delegate;

    // Cached attributes retrieved natively from java.io.File
    private final boolean isDirectory;
    private final boolean isFile;
    private final long length;
    private final FDate lastModified;

    public IoFileInfo(final String serverUri, final String directory, final File delegate) {
        this.serverUri = serverUri;
        this.directory = directory;
        this.delegate = delegate;

        this.isDirectory = delegate.isDirectory();
        this.isFile = delegate.isFile();
        this.length = delegate.length();
        this.lastModified = new FDate(delegate.lastModified());
    }

    @Override
    public String getServerUri() {
        return serverUri;
    }

    @Override
    public String getDirectory() {
        return directory;
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

    public static IoFileInfo valueOf(final String serverUri, final String path, final File file) {
        if (file == null) {
            return null;
        }
        return new IoFileInfo(serverUri, path, file);
    }
}