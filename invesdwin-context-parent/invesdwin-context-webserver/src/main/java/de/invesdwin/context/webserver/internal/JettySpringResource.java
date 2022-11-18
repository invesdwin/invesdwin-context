package de.invesdwin.context.webserver.internal;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.channels.ReadableByteChannel;

import javax.annotation.concurrent.NotThreadSafe;

import org.eclipse.jetty.util.resource.Resource;

import de.invesdwin.context.beans.init.PreMergedContext;

@NotThreadSafe
public class JettySpringResource extends Resource {

    private final org.springframework.core.io.Resource resource;

    public JettySpringResource(final String path) {
        this.resource = PreMergedContext.getInstance().getResource(path);
    }

    @Override
    public boolean isContainedIn(final Resource r) throws MalformedURLException {
        return false;
    }

    @Override
    public void close() {}

    @Override
    public boolean exists() {
        return resource.exists();
    }

    @Override
    public boolean isDirectory() {
        return false;
    }

    @Override
    public long lastModified() {
        try {
            return resource.lastModified();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long length() {
        try {
            return resource.lastModified();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public File getFile() throws IOException {
        return resource.getFile();
    }

    @Override
    public String getName() {
        return resource.getFilename();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return resource.getInputStream();
    }

    @Override
    public ReadableByteChannel getReadableByteChannel() throws IOException {
        return null;
    }

    @Override
    public boolean delete() throws SecurityException {
        return false;
    }

    @Override
    public boolean renameTo(final Resource dest) throws SecurityException {
        return false;
    }

    @Override
    public String[] list() {
        return null;
    }

    @Override
    public Resource addPath(final String path) throws IOException, MalformedURLException {
        return null;
    }

    @Override
    public String toString() {
        return resource.toString();
    }

    @Override
    public URI getURI() {
        try {
            return resource.getURI();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

}
