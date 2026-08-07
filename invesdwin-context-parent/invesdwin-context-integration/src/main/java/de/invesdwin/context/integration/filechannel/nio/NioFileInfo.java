package de.invesdwin.context.integration.filechannel.nio;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.context.integration.filechannel.info.FileChannelInfos;
import de.invesdwin.context.integration.filechannel.info.IFileInfo;
import de.invesdwin.util.lang.Files;
import de.invesdwin.util.time.date.FDate;

@Immutable
public class NioFileInfo implements IFileInfo {

    private final String serverUri;
    private final String directory;

    // Extracted the filename as a native String to ensure safe serialization
    private final String filename;

    // Marked transient because java.nio.file.Path is not Serializable
    // Removed the 'final' modifier so it can be lazily reconstructed
    private transient Path delegate;

    private final boolean isDirectory;
    private final boolean isFile;
    private final long length;
    private final FDate lastModified;

    public NioFileInfo(final String serverUri, final String directory, final Path delegate) {
        this.serverUri = serverUri;
        this.directory = directory;
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
    public String getServerUri() {
        return serverUri;
    }

    @Override
    public String getDirectory() {
        return directory;
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
        // Lazily reconstruct the Path if this object was sent over the wire
        if (delegate == null) {
            final String uriStr = FileChannelInfos.newFileUri(getServerUri(), getDirectory(), getFilename())
                    .replaceAll("(?<!:)/{3,}", "///");
            delegate = Paths.get(URI.create(uriStr));
        }
        return delegate;
    }

    @Override
    public String toString() {
        return FileChannelInfos.toString(this);
    }

    public static NioFileInfo valueOf(final String serverUri, final String path, final Path file) {
        if (file == null) {
            return null;
        }
        return new NioFileInfo(serverUri, path, file);
    }
}