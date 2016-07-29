package de.invesdwin.context.integration.csv;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.annotation.concurrent.ThreadSafe;

import org.junit.Test;

import de.invesdwin.context.test.ATest;

@ThreadSafe
public class CsvVerificationTest extends ATest {

    @Test
    public void testIsCsv() {
        CsvVerification csvVeri = new CsvVerification(1, ";");
        assertTrue(csvVeri.isCsv("asd"));
        assertFalse(csvVeri.isCsv("asd;"));
        assertFalse(csvVeri.isCsv("asd;asd;asd"));

        csvVeri = new CsvVerification(2, ";");
        assertTrue(csvVeri.isCsv("two;two"));
        assertTrue(csvVeri.isCsv("two;"));
        assertTrue(csvVeri.isCsv(";two"));
        assertFalse(csvVeri.isCsv("two"));

        assertFalse(csvVeri.isCsv(null));
        assertFalse(csvVeri.isCsv("  "));
    }

}
