package de.invesdwin.context.system;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import de.invesdwin.context.ContextProperties;
import de.invesdwin.context.log.error.Err;
import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.concurrent.Executors;
import de.invesdwin.util.concurrent.WrappedExecutorService;
import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.lang.UniqueNameGenerator;

/**
 * Behaves just like a SoftReference, with the distinction that the value is not discarded, but instead serialized until
 * it is requested again.
 * 
 * Thus this reference will never return null if the referent was not null in the first place.
 * 
 * <a href="http://stackoverflow.com/questions/10878012/using-referencequeue-and-weakreference">Source</a>
 */
@NotThreadSafe
public class SerializingSoftReference<T extends Serializable> extends SoftReference<T> {

    private static final ReferenceQueue<Object> REAPED_QUEUE = new ReferenceQueue<Object>();
    private static final UniqueNameGenerator UNIQUE_NAME_GENERATOR = new UniqueNameGenerator();
    private File file;
    private DelegateSoftReference<T> delegate;

    static {
        final WrappedExecutorService executor = Executors
                .newFixedCallerRunsThreadPool(SerializingSoftReference.class.getSimpleName(), 1);
        executor.execute(new Runnable() {
            @SuppressWarnings("unchecked")
            @Override
            public void run() {
                try {
                    while (true) {
                        final DelegateSoftReference<? extends Object> removed = (DelegateSoftReference<? extends Object>) REAPED_QUEUE
                                .remove();
                        removed.clear();
                    }
                } catch (final InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
    }

    public SerializingSoftReference(final T referent) {
        super((T) null);
        delegate = new DelegateSoftReference<T>(this, referent);
        if (referent != null) {
            file = new File(getTempFolder(), UNIQUE_NAME_GENERATOR.get(referent.getClass().getSimpleName()) + ".data");
        } else {
            //deactivated
            file = null;
        }
    }

    private File getTempFolder() {
        final File tempFolder = new File(ContextProperties.TEMP_DIRECTORY,
                SerializingSoftReference.class.getSimpleName());
        try {
            FileUtils.forceMkdir(tempFolder);
        } catch (final IOException e) {
            throw Err.process(e);
        }
        return tempFolder;
    }

    @Override
    public T get() {
        if (delegate == null) {
            if (file == null) {
                return (T) null;
            }
            readReferent();
        }
        return delegate.hardReferent;
    }

    private void writeReferent() {
        final T referent = delegate.hardReferent;
        Assertions.assertThat((referent == null) == (file == null)).isTrue();
        if (referent != null) {
            OutputStream out = null;
            try {
                out = new GZIPOutputStream(new FileOutputStream(file));
                out.write(serialize(referent));
                delegate = null;
            } catch (final FileNotFoundException e) {
                throw Err.process(e);
            } catch (final IOException e) {
                throw Err.process(e);
            } finally {
                IOUtils.closeQuietly(out);
            }
        }
    }

    protected byte[] serialize(final T referent) {
        return Objects.serialize(referent);
    }

    protected T deserialize(final InputStream in) {
        return Objects.deserialize(in);
    }

    private void readReferent() {
        InputStream in = null;
        try {
            in = new GZIPInputStream(new FileInputStream(file));
            final T referent = deserialize(in);
            delegate = new DelegateSoftReference<T>(this, referent);
        } catch (final FileNotFoundException e) {
            throw Err.process(e);
        } catch (final IOException e) {
            throw Err.process(e);
        } finally {
            IOUtils.closeQuietly(in);
        }
        FileUtils.deleteQuietly(file);
    }

    /**
     * This can be used to manually serialize the object.
     */
    @Override
    public void clear() {
        if (delegate != null) {
            delegate.clear();
        }
        super.clear();
    }

    /**
     * Discards this reference and deletes the serialized file if it exists.
     */
    public void close() {
        delegate = null;
        FileUtils.deleteQuietly(file);
        file = null;
    }

    private static class DelegateSoftReference<T extends Serializable> extends SoftReference<WrappedReferent<T>> {

        private final SerializingSoftReference<T> parent;
        private final T hardReferent;

        DelegateSoftReference(final SerializingSoftReference<T> parent, final T referent) {
            super(new WrappedReferent<T>(referent), REAPED_QUEUE);
            this.parent = parent;
            this.hardReferent = referent;
        }

        @Override
        public void clear() {
            parent.writeReferent();
            super.clear();
        }

    }

    private static class WrappedReferent<T> {
        @SuppressWarnings("unused")
        private final T referent;

        WrappedReferent(final T referent) {
            this.referent = referent;
        }

    }

}
