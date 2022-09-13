package de.invesdwin.context.integration.compression;

import java.io.IOException;

import javax.annotation.concurrent.ThreadSafe;

import org.junit.jupiter.api.Test;

import de.invesdwin.context.test.ATest;

// CHECKSTYLE:OFF
@ThreadSafe
public class ADecompressingInputStreamTest extends ATest {
    //CHECKSTYLE:ON

    @Test
    public void testDecompression() throws IOException {
        //        IOUtils.copy(new ADecompressingInputStream(new TextDescription("asdf")) {
        //            @Override
        //            protected java.io.InputStream innerNewDelegate() {
        //                try {
        //                    return new FileInputStream(new File("/home/subes/Desktop/sound/mktsymbols_v2.zip"));
        //                } catch (final FileNotFoundException e) {
        //                    throw new RuntimeException(e);
        //                }
        //            }
        //        }, new FileOutputStream(new File("/home/subes/Desktop/sound/mktsymbols_v2.txt")));
    }
}
