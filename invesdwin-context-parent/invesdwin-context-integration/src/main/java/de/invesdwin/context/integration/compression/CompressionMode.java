package de.invesdwin.context.integration.compression;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.context.integration.compression.lz4.FastLZ4CompressionFactory;
import de.invesdwin.context.integration.compression.lz4.HighLZ4CompressionFactory;
import de.invesdwin.context.integration.compression.lz4.LZ4Streams;

@Immutable
public enum CompressionMode {
    HIGH {
        @Override
        public ICompressionFactory newCompressionFactory() {
            return HighLZ4CompressionFactory.INSTANCE;
        }
    },
    FAST {
        @Override
        public ICompressionFactory newCompressionFactory() {
            return FastLZ4CompressionFactory.INSTANCE;
        }
    },
    NONE {
        @Override
        public ICompressionFactory newCompressionFactory() {
            return DisabledCompressionFactory.INSTANCE;
        }
    },
    DEFAULT {
        @Override
        public ICompressionFactory newCompressionFactory() {
            return LZ4Streams.getDefaultCompressionFactory();
        }
    };

    public abstract ICompressionFactory newCompressionFactory();

}
