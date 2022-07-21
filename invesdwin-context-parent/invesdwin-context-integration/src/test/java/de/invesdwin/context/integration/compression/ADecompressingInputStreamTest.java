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
        //            IOUtils.copy(new DecompressingInputStream(new FileInputStream(new File("/home/subes/Desktop/allCountries.zip"))),
        //                    new FileOutputStream(new File("/home/subes/Desktop/allCountriesTest.txt")));
    }
}
