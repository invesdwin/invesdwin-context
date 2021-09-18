package de.invesdwin.context.log.error;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.context.ContextProperties;
import de.invesdwin.util.lang.Strings;

@Immutable
final class ThrowableConverter {

    private static final StackTraceElement[] ELEMENT_EMTPY_ARRAY = new StackTraceElement[0];
    private static final String STACKTRACE_LINE_MARKED = "\n      * ";
    private static final String STACKTRACE_LINE = "\n        ";

    private ThrowableConverter() {}

    public static String loggedRuntimeExceptionToString(final LoggedRuntimeException e, final boolean detailed) {
        final StringBuilder s = new StringBuilder("processing ");
        s.append(e.getIdString());

        s.append(throwableToString(e, detailed));

        if (detailed) {
            s.append("\n\n\n");
        }

        return s.toString();
    }

    public static String throwableToString(final Throwable e, final boolean detailed) {
        final StringBuilder s = new StringBuilder();
        Throwable cause = e;
        while (cause != null) {
            s.append("\n");
            //erster cause wird als eigentliche exception geloggt
            if (cause != e) {
                s.append("Caused by - ");
            }
            s.append(cause.getClass().getName());
            s.append(": ");
            s.append(cause.getMessage());
            s.append(stackTraceToString(cause, detailed));
            cause = cause.getCause();
        }
        return s.toString();
    }

    private static String stackTraceToString(final Throwable e, final boolean detailed) {
        final StringBuilder s = new StringBuilder();
        final StackTraceElement[] trace = getStackTrace(e, detailed);

        for (int i = 0; i < trace.length; i++) {
            final boolean isBasePackageTrace = isBasePackageTrace(trace[i]);
            if (isBasePackageTrace) {
                s.append(STACKTRACE_LINE_MARKED);
            } else {
                s.append(STACKTRACE_LINE);
            }
            s.append("at ");
            s.append(trace[i].toString());
            if (isBasePackageTrace) {
                s.append(" *");
            }
        }
        if (!detailed) {
            final int countMore = e.getStackTrace().length - trace.length;
            if (countMore > 0) {
                s.append(STACKTRACE_LINE);
                s.append("... ");
                s.append(countMore);
                if (e instanceof LoggedRuntimeException) {
                    s.append(" omitted, see following cause or error.log");
                } else {
                    s.append(" more, see error.log");
                }
            }
        }
        return s.toString();
    }

    private static StackTraceElement[] getStackTrace(final Throwable e, final boolean detailed) {
        if (!detailed && e instanceof LoggedRuntimeException) {
            return ELEMENT_EMTPY_ARRAY;
        } else if (detailed) {
            return e.getStackTrace();
        } else {
            final List<StackTraceElement> stack = new ArrayList<StackTraceElement>(Arrays.asList(e.getStackTrace()));
            int i = stack.size() - 1;
            while (i >= 0) {
                if (isBasePackageTrace(stack.get(i))) {
                    break;
                } else {
                    stack.remove(i);
                    i--;
                }
            }
            return stack.toArray(ELEMENT_EMTPY_ARRAY);
        }
    }

    private static boolean isBasePackageTrace(final StackTraceElement e) {
        String str = e.toString();
        // workaround for app//de.invesdwin... on newer JDKs
        if (str.contains("/")) {
            str = Strings.substringAfterLast(str, "/");
        }
        return Strings.startsWithAny(str, ContextProperties.getBasePackagesArray());
    }

}
