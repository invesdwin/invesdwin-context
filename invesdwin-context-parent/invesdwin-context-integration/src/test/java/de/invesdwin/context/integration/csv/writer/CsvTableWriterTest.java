package de.invesdwin.context.integration.csv.writer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.Test;

import de.invesdwin.context.test.ATest;

@NotThreadSafe
public class CsvTableWriterTest extends ATest {

    @Test
    public void test() throws IOException {
        final StringBuilder sb = new StringBuilder();
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        final CsvTableWriter writer = new CsvTableWriter(bos);
        writer.line("one", "two", "three");
        for (int i = 0; i < 20; i++) {
            writer.line(i, "2-" + i, "three-" + i);
        }
        writer.close();
        log.info("Table:\n" + new String(bos.toByteArray()));
    }

}
