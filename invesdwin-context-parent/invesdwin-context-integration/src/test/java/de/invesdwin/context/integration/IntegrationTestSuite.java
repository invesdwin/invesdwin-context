package de.invesdwin.context.integration;

import javax.annotation.concurrent.Immutable;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.invesdwin.context.integration.csv.CsvItemReaderBuilderTest;
import de.invesdwin.context.integration.csv.CsvVerificationTest;
import de.invesdwin.context.integration.csv.writer.AsciiTableWriterTest;
import de.invesdwin.context.integration.csv.writer.CsvTableWriterTest;
import de.invesdwin.context.integration.csv.writer.HtmlTableWriterTest;
import de.invesdwin.context.integration.marshaller.MarshallersTest;
import de.invesdwin.context.integration.network.NetworkUtilTest;
import de.invesdwin.context.integration.streams.DecompressingInputStreamTest;

@RunWith(Suite.class)
@SuiteClasses({ CsvVerificationTest.class, CsvItemReaderBuilderTest.class, DecompressingInputStreamTest.class,
        MarshallersTest.class, IntegrationTest.class, NetworkUtilTest.class, AsciiTableWriterTest.class,
        CsvTableWriterTest.class, HtmlTableWriterTest.class })
@Immutable
public class IntegrationTestSuite {

}
