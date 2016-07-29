package de.invesdwin.context.integration.streams;

import java.io.IOException;

import javax.annotation.concurrent.ThreadSafe;

import org.junit.Test;

import de.invesdwin.context.test.ATest;

@ThreadSafe
public class DecompressingInputStreamTest extends ATest {

    @Test
    public void testDecompression() throws IOException {
        //            IOUtils.copy(new DecompressingInputStream(new FileInputStream(new File("/home/subes/Desktop/allCountries.zip"))),
        //                    new FileOutputStream(new File("/home/subes/Desktop/allCountriesTest.txt")));
    }
}
