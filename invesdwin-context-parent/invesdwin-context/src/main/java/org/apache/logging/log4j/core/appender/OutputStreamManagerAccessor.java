package org.apache.logging.log4j.core.appender;

import java.io.OutputStream;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.logging.log4j.core.Layout;

@NotThreadSafe
public class OutputStreamManagerAccessor extends OutputStreamManager {

    public OutputStreamManagerAccessor(final OutputStream os, final String streamName, final Layout<?> layout,
            final boolean writeHeader) {
        super(os, streamName, layout, writeHeader);
    }

}
