package de.invesdwin.context.integration.csv.writer;

import java.io.IOException;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.Test;

import de.invesdwin.context.test.ATest;

@NotThreadSafe
public class AsciiTableWriterTest extends ATest {

    @Test
    public void test() throws IOException {
        final StringBuilder sb = new StringBuilder();
        final AsciiTableWriter writer = new AsciiTableWriter(sb);
        writer.line("one", "two", "three");
        for (int i = 0; i < 20; i++) {
            writer.line(i, "2-" + i, "three-" + i);
        }
        writer.close();
        log.info("Table:\n" + sb.toString());
    }

}
