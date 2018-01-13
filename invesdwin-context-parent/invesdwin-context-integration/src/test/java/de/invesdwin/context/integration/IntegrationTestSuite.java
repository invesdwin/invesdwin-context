package de.invesdwin.context.integration;

import javax.annotation.concurrent.Immutable;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.invesdwin.context.integration.csv.CsvItemReaderBuilderTest;
import de.invesdwin.context.integration.csv.CsvVerificationTest;
import de.invesdwin.context.integration.network.NetworkUtilTest;
import de.invesdwin.context.integration.streams.DecompressingInputStreamTest;

@RunWith(Suite.class)
@SuiteClasses({ CsvVerificationTest.class, DecompressingInputStreamTest.class, MarshallersTest.class,
        CsvItemReaderBuilderTest.class, IntegrationTest.class, NetworkUtilTest.class })
@Immutable
public class IntegrationTestSuite {

}
