package de.invesdwin.context.graalvm.jsr223;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class PolyglotReader extends InputStream {
    private volatile Reader reader;

    public PolyglotReader(final InputStreamReader inputStreamReader) {
        this.reader = inputStreamReader;
    }

    @Override
    public int read() throws IOException {
        return reader.read();
    }

    public Reader getReader() {
        return reader;
    }

    public void setReader(final Reader reader) {
        this.reader = reader;
    }
}