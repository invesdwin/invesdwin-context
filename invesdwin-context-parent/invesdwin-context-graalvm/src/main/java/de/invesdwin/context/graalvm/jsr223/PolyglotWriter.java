package de.invesdwin.context.graalvm.jsr223;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class PolyglotWriter extends OutputStream {
    private volatile Writer writer;

    public PolyglotWriter(final OutputStreamWriter outputStreamWriter) {
        this.writer = outputStreamWriter;
    }

    @Override
    public void write(final int b) throws IOException {
        writer.write(b);
    }

    public Writer getWriter() {
        return writer;
    }

    public void setWriter(final Writer writer) {
        this.writer = writer;
    }
}