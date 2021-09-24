package de.invesdwin.context.beans.init.locations;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import javax.annotation.concurrent.NotThreadSafe;

import org.springframework.core.io.Resource;

import de.invesdwin.context.beans.init.locations.position.ResourcePosition;
import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.lang.comparator.IComparator;

/**
 * Marker interface to make ContextLocations load before or after others. Useful for infrastructure spring configs where
 * the loading order is relevant.
 * 
 * @author subes
 * 
 */
@NotThreadSafe
public final class PositionedResource implements Resource, Comparable<Resource> {

    public static final IComparator<PositionedResource> COMPARATOR = IComparator.getDefaultInstance();

    private final Resource delegate;
    private final ResourcePosition position;

    private PositionedResource(final Resource delegate, final ResourcePosition position) {
        this.delegate = delegate;
        this.position = position;
    }

    public ResourcePosition getPosition() {
        return position;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return delegate.getInputStream();
    }

    @Override
    public boolean exists() {
        return delegate.exists();
    }

    @Override
    public boolean isReadable() {
        return delegate.isReadable();
    }

    @Override
    public boolean isOpen() {
        return delegate.isOpen();
    }

    @Override
    public URL getURL() throws IOException {
        return delegate.getURL();
    }

    @Override
    public URI getURI() throws IOException {
        return delegate.getURI();
    }

    @Override
    public File getFile() throws IOException {
        return delegate.getFile();
    }

    @Override
    public long contentLength() throws IOException {
        return delegate.contentLength();
    }

    @Override
    public long lastModified() throws IOException {
        return delegate.lastModified();
    }

    @Override
    public Resource createRelative(final String relativePath) throws IOException {
        return delegate.createRelative(relativePath);
    }

    @Override
    public String getFilename() {
        return delegate.getFilename();
    }

    @Override
    public String getDescription() {
        return delegate.getDescription();
    }

    @Override
    public String toString() {
        return delegate.toString();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof PositionedResource) {
            final PositionedResource cObj = (PositionedResource) obj;
            return delegate.equals(cObj.delegate);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public int compareTo(final Resource o) {
        final ResourcePosition oPosition;
        if (o instanceof PositionedResource) {
            final PositionedResource cO = (PositionedResource) o;
            oPosition = cO.getPosition();
        } else {
            oPosition = ResourcePosition.DEFAULT;
        }
        return getPosition().compareTo(oPosition);
    }

    public static PositionedResource of(final Resource resource, final ResourcePosition position) {
        Assertions.assertThat(resource).isNotInstanceOf(PositionedResource.class);
        return new PositionedResource(resource, position);
    }

    public static PositionedResource of(final Resource resource) {
        return of(resource, ResourcePosition.DEFAULT);
    }

}