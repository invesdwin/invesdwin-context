package de.invesdwin.context.integration;

import javax.annotation.concurrent.Immutable;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

import de.invesdwin.context.integration.compression.CommonsDecompressingInputStreamTest;
import de.invesdwin.context.integration.csv.CsvItemReaderBuilderTest;
import de.invesdwin.context.integration.csv.CsvVerificationTest;
import de.invesdwin.context.integration.csv.writer.AsciiTableWriterTest;
import de.invesdwin.context.integration.csv.writer.CsvTableWriterTest;
import de.invesdwin.context.integration.csv.writer.HtmlTableWriterTest;
import de.invesdwin.context.integration.marshaller.MarshallersTest;
import de.invesdwin.context.integration.network.NetworkUtilTest;

@Suite
@SelectClasses({ CsvVerificationTest.class, CsvItemReaderBuilderTest.class, CommonsDecompressingInputStreamTest.class,
        MarshallersTest.class, IntegrationTest.class, NetworkUtilTest.class, AsciiTableWriterTest.class,
        CsvTableWriterTest.class, HtmlTableWriterTest.class })
@Immutable
public class IntegrationTestSuite {

}
