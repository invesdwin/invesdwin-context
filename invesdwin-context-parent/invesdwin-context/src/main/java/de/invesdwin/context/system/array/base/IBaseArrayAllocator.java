package de.invesdwin.context.system.array.base;

import java.io.Closeable;
import java.io.File;

import de.invesdwin.context.system.properties.IProperties;
import de.invesdwin.norva.beanpath.spi.IUnwrap;
import de.invesdwin.util.collections.attributes.IAttributesMap;
import de.invesdwin.util.concurrent.lock.ILock;

/**
 * An instance of a primitive array allocator should only be shared within the same backtesting engine. Otherwise
 * engines might get confused when accessing and sharing cached time indexes.
 */
public interface IBaseArrayAllocator extends IUnwrap, Closeable {

    IAttributesMap getAttributes();

    IProperties getProperties();

    void clear();

    File getDirectory();

    @Override
    void close();

    ILock getLock(String id);

}
