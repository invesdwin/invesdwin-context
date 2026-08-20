package de.invesdwin.context.integration.filechannel.nio;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.context.integration.filechannel.info.IFileInfo;
import de.invesdwin.context.integration.filechannel.info.path.FileChannelPaths;
import de.invesdwin.util.lang.Files;
import de.invesdwin.util.time.date.FDate;

@Immutable
public class NioFileInfo implements IFileInfo {

    private final URI serverUri;
    private final URI baseServerUri;
    private final String baseDirectory;
    private final String subDirectory;
    private final String filename;
    private transient Path delegate;

    private final boolean isDirectory;
    private final boolean isFile;
    private final long length;
    private final FDate lastModified;

    public NioFileInfo(final URI serverUri, final URI baseServerUri, final String baseDirectory,
            final String subDirectory, final Path delegate) {
        this.serverUri = serverUri;
        this.baseServerUri = baseServerUri;
        this.baseDirectory = baseDirectory;
        this.subDirectory = subDirectory;
        this.delegate = delegate;
        this.filename = delegate.getFileName().toString();

        try {
            final BasicFileAttributes attrs = Files.readAttributes(delegate, BasicFileAttributes.class);
            this.isDirectory = attrs.isDirectory();
            this.isFile = attrs.isRegularFile();
            this.length = attrs.size();
            this.lastModified = new FDate(attrs.lastModifiedTime().toMillis());
        } catch (final IOException e) {
            throw new RuntimeException("Failed to read attributes for path: " + delegate, e);
        }
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
    public String getFilename() {
        return filename;
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
    public Path unwrap() {
        if (delegate == null) {
            delegate = Paths.get(getFileUri());
        }
        return delegate;
    }

    @Override
    public String toString() {
        return FileChannelPaths.toString(this);
    }

    public static NioFileInfo valueOf(final URI serverUri, final URI baseServerUri, final String baseDirectory,
            final String subDirectory, final Path file) {
        if (file == null) {
            return null;
        }
        return new NioFileInfo(serverUri, baseServerUri, baseDirectory, subDirectory, file);
    }
}