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

    // Lazy and transient fields
    private transient volatile BasicFileAttributes attributes;

    public NioFileInfo(final URI serverUri, final URI baseServerUri, final String baseDirectory,
            final String subDirectory, final Path delegate) {
        this.serverUri = serverUri;
        this.baseServerUri = baseServerUri;
        this.baseDirectory = baseDirectory;
        this.subDirectory = subDirectory;
        this.delegate = delegate;
        this.filename = delegate.getFileName().toString();
        // Removed eager file system I/O evaluation from the constructor
    }

    private BasicFileAttributes getAttributes() {
        BasicFileAttributes attrs = attributes;
        if (attrs == null) {
            try {
                attrs = Files.readAttributes(unwrap(), BasicFileAttributes.class);
            } catch (final IOException e) {
                attrs = DisabledBasicFileAttributes.INSTANCE;
            }
            attributes = attrs;
        }
        return attrs;
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
        return filename;
    }

    @Override
    public boolean isFile() {
        return getAttributes().isRegularFile();
    }

    @Override
    public boolean isDirectory() {
        return getAttributes().isDirectory();
    }

    @Override
    public FDate lastModified() {
        return new FDate(getAttributes().lastModifiedTime().toMillis());
    }

    @Override
    public long length() {
        return getAttributes().size();
    }

    @Override
    public Path unwrap() {
        if (delegate == null) {
            delegate = Paths.get(getFileUri());
        }
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

    public static NioFileInfo valueOf(final URI serverUri, final URI baseServerUri, final String baseDirectory,
            final String subDirectory, final Path file) {
        if (file == null) {
            return null;
        }
        return new NioFileInfo(serverUri, baseServerUri, baseDirectory, subDirectory, file);
    }
}