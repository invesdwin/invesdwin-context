package de.invesdwin.context.log.error;

import java.text.DecimalFormat;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public final class LoggedRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private static final AtomicInteger ID = new AtomicInteger();

    private final int id;

    private LoggedRuntimeException(final int id, final Throwable t) {
        super(createLoggedMessage(id, t), t);
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getIdString() {
        return idToString(id).toString();
    }

    public String getIdTrace() {
        return createIdTrace(getId(), getCause()).toString();
    }

    static LoggedRuntimeException newInstance(final Throwable t) {
        final int id = ID.incrementAndGet();
        return new LoggedRuntimeException(id, t);
    }

    private static String createLoggedMessage(final int id, final Throwable t) {
        final StringBuilder s = createIdTrace(id, t);
        s.append(" ");
        s.append(t.getClass().getName());
        s.append(": ");
        s.append(t.getMessage());
        return s.toString();
    }

    private static StringBuilder createIdTrace(final int id, final Throwable t) {
        final StringBuilder s = idToString(id);
        Throwable cause = t;
        while (cause != null) {
            if (cause instanceof LoggedRuntimeException) {
                final int causeId = ((LoggedRuntimeException) cause).getId();
                s.append("->");
                s.append(idToString(causeId));
            }
            cause = cause.getCause();
        }
        return s;
    }

    private static StringBuilder idToString(final int id) {
        final StringBuilder s = new StringBuilder("#");
        s.append(new DecimalFormat("00000000").format(id));
        return s;
    }

}
