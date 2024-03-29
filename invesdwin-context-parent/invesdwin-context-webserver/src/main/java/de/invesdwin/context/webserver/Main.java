package de.invesdwin.context.webserver;

import javax.annotation.concurrent.ThreadSafe;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import de.invesdwin.context.beans.init.AMain;
import de.invesdwin.context.beans.init.MergedContext;

@ThreadSafe
public final class Main extends AMain {

    private Main(final String[] args) {
        super(args, false);
    }

    public static void main(final String[] args) {
        new Main(args).run();
    }

    @Override
    protected void startApplication(final CmdLineParser parser) throws CmdLineException {
        WebserverContextLocation.activate();
        MergedContext.autowire(this);
        waitForShutdown();
    }

}
