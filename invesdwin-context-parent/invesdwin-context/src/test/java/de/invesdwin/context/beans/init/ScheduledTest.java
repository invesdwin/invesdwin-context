package de.invesdwin.context.beans.init;

import javax.annotation.concurrent.ThreadSafe;
import javax.inject.Inject;

import org.junit.Ignore;
import org.junit.Test;

import de.invesdwin.context.test.ATest;
import de.invesdwin.context.test.TestContext;

@Ignore("needs to be called manually since not working reliably in suite")
@ThreadSafe
public class ScheduledTest extends ATest {

    @Inject
    private ScheduledTestBean scheduledTestBean;

    @Override
    public void setUpContext(final TestContext ctx) throws Exception {
        super.setUpContext(ctx);
        ctx.activate(ScheduledTestBean.class);
    }

    @Test
    public void testScheduled() throws InterruptedException {
        scheduledTestBean.testScheduled();
    }

}
