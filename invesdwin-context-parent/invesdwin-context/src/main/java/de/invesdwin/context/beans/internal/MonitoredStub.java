package de.invesdwin.context.beans.internal;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import javax.annotation.concurrent.Immutable;
import javax.inject.Named;

import com.jamonapi.MonitorFactory;

import de.invesdwin.context.ContextProperties;
import de.invesdwin.context.test.ATest;
import de.invesdwin.context.test.TestContext;
import de.invesdwin.context.test.stub.StubSupport;
import de.invesdwin.util.lang.Files;
import de.invesdwin.util.lang.Strings;

@Immutable
@Named
public class MonitoredStub extends StubSupport {

    static {
        //monitorfactory is enabled per default
        MonitorFactory.disable();
    }

    @Override
    public void tearDown(final ATest test, final TestContext ctx) throws IOException {
        if (MonitorFactory.isEnabled()) {
            final String report = MonitorFactory.getRootMonitor().getReport();
            if (!Strings.isBlank(report)) {
                Files.writeStringToFile(new File(ContextProperties.getCacheDirectory(), "Monitoring.html"), report,
                        Charset.defaultCharset());
            }
        }
        MonitorFactory.reset();
        MonitorFactory.disable();
    }

}
