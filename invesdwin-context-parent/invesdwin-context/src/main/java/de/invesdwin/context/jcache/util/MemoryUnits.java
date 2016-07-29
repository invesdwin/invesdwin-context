package de.invesdwin.context.jcache.util;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.error.UnknownArgumentException;
import de.invesdwin.util.math.decimal.scaled.ByteSizeScale;

@Immutable
public final class MemoryUnits {

    private MemoryUnits() {}

    public static net.sf.ehcache.config.MemoryUnit toEhCache(final ByteSizeScale scale) {
        switch (scale) {
        case BYTES:
            return net.sf.ehcache.config.MemoryUnit.BYTES;
        case KILOBYTES:
            return net.sf.ehcache.config.MemoryUnit.KILOBYTES;
        case MEGABYTES:
            return net.sf.ehcache.config.MemoryUnit.MEGABYTES;
        case GIGABYTES:
        case TERABYTES:
        case PETABYTES:
            return net.sf.ehcache.config.MemoryUnit.GIGABYTES;
        default:
            throw UnknownArgumentException.newInstance(ByteSizeScale.class, scale);
        }
    }

    public static ByteSizeScale fromEhCache(final net.sf.ehcache.config.MemoryUnit unit) {
        switch (unit) {
        case BYTES:
            return ByteSizeScale.BYTES;
        case KILOBYTES:
            return ByteSizeScale.KILOBYTES;
        case MEGABYTES:
            return ByteSizeScale.MEGABYTES;
        case GIGABYTES:
            return ByteSizeScale.GIGABYTES;
        default:
            throw UnknownArgumentException.newInstance(net.sf.ehcache.config.MemoryUnit.class, unit);
        }
    }

}
