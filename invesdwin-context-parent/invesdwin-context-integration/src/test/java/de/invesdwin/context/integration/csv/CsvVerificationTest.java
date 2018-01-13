package de.invesdwin.context.integration.csv;

import javax.annotation.concurrent.ThreadSafe;

import org.junit.Test;

import de.invesdwin.context.test.ATest;
import de.invesdwin.util.assertions.Assertions;

@ThreadSafe
public class CsvVerificationTest extends ATest {

    @Test
    public void testIsCsv() {
        CsvVerification csvVeri = new CsvVerification(1, ";");
        Assertions.checkTrue(csvVeri.isCsv("asd"));
        Assertions.checkFalse(csvVeri.isCsv("asd;"));
        Assertions.checkFalse(csvVeri.isCsv("asd;asd;asd"));

        csvVeri = new CsvVerification(2, ";");
        Assertions.checkTrue(csvVeri.isCsv("two;two"));
        Assertions.checkTrue(csvVeri.isCsv("two;"));
        Assertions.checkTrue(csvVeri.isCsv(";two"));
        Assertions.checkFalse(csvVeri.isCsv("two"));

        Assertions.checkFalse(csvVeri.isCsv(null));
        Assertions.checkFalse(csvVeri.isCsv("  "));
    }

}
