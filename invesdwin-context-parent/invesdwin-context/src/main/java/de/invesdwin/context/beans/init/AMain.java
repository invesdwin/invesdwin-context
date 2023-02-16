package de.invesdwin.context.beans.init;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.annotation.concurrent.Immutable;

import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import de.invesdwin.context.log.Log;
import de.invesdwin.context.log.error.Err;
import de.invesdwin.context.log.error.LoggedRuntimeException;
import de.invesdwin.context.system.properties.ResourceBundles;
import de.invesdwin.context.system.properties.SystemProperties;
import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.lang.string.Strings;
import de.invesdwin.util.time.date.FTimeUnit;

@Immutable
public abstract class AMain {

    protected final Log log = new Log(this);

    @Option(help = true, name = "-h", aliases = "--help", usage = "Shows this help text")
    protected boolean help;

    protected final String[] args;

    protected AMain(final String[] args) {
        this(args, true);
    }

    protected AMain(final String[] args, final boolean bootstrap) {
        this.args = args;
        parseCommandline(args, bootstrap);
        Assertions.assertThat(PreMergedContext.class).isNotNull();
    }

    private void parseCommandline(final String[] args, final boolean bootstrap) {
        final CmdLineParser parser = newCmdLineParser();
        try {
            final String[] filteredArgs = parseSystemProperties(args);
            parser.parseArgument(filteredArgs);
            if (help) {
                printHelp(parser);
            } else {
                if (bootstrap) {
                    MergedContext.autowire(this);
                }
                startApplication(parser);
            }
        } catch (final RuntimeException e) {
            throw Err.process(e);
        } catch (final Exception e) {
            final LoggedRuntimeException processed = Err.process(e);
            printHelp(parser);
            throw processed; //for the case when the main method was called directly
        } catch (final Throwable e) {
            throw Err.process(e);
        }
    }

    protected CmdLineParser newCmdLineParser() {
        return new CmdLineParser(this);
    }

    /**
     * Checked exceptions will be logged with help text, runtime exceptions will only be logged without help text.
     */
    protected abstract void startApplication(CmdLineParser parser) throws Exception;

    protected final void waitForShutdown() {
        try {
            while (true) {
                FTimeUnit.YEARS.sleep(1);
            }
        } catch (final InterruptedException e) {
            throw Err.process(e);
        }
    }

    /**
     * @see <a href=
     *      "http://commons.apache.org/jelly/xref/org/apache/commons/jelly/util/CommandLineParser.html">Source</a>
     */
    protected final String[] parseSystemProperties(final String[] args) {
        final SystemProperties sysProps = new SystemProperties();

        // filter the system property setting from the arg list
        // before passing it to the CLI parser
        final List<String> filteredArgList = new ArrayList<String>();

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];

            // if this is a -D property parse it and add it to the system properties.
            // -D args will not be copied into the filteredArgList.
            if (arg.startsWith("-D") && (arg.length() > 2)) {
                arg = arg.substring(2);
                final int ePos = arg.indexOf("=");
                if (ePos == -1 || ePos == 0 || ePos == arg.length() - 1) {
                    throw new IllegalArgumentException("Invalid system property: \"" + arg + "\".");
                }
                sysProps.setString(arg.substring(0, ePos), arg.substring(ePos + 1));
            } else {
                // add this to the filtered list of arguments
                filteredArgList.add(arg);
            }
        }

        // make the filteredArgList into an array
        return filteredArgList.toArray(Strings.EMPTY_ARRAY);
    }

    protected final void printHelp(final CmdLineParser parser) {
        log.error(createHelpString(parser));
    }

    protected String createHelpString(final CmdLineParser parser) {
        final StringWriter writer = new StringWriter();
        parser.setUsageWidth(120);
        final ResourceBundle rb = ResourceBundles.getResourceBundle(getClass());
        writer.append("\nUsage:\tjava ");
        writer.append(getClass().getName());
        parser.printSingleLineUsage(writer, rb);
        writer.append("\nOptions:\n");
        parser.printUsage(writer, rb);
        final String help = writer.toString();
        return help;
    }
}
