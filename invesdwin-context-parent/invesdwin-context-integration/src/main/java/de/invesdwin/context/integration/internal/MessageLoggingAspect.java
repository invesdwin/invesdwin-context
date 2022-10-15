package de.invesdwin.context.integration.internal;

import javax.annotation.concurrent.ThreadSafe;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import de.invesdwin.context.log.Log;
import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.lang.string.Strings;

@ThreadSafe
@Aspect
public class MessageLoggingAspect {

    private static final String SERVICE = "SERVICE";
    private static final String CLIENT = "CLIENT ";
    private static final String IN = "IN ";
    private static final String OUT = "OUT";
    private final Log log = new Log("de.invesdwin.MESSAGES");

    @Around("@annotation(org.springframework.integration.annotation.Gateway)")
    public Object client(final ProceedingJoinPoint pjp) throws Throwable {
        Assertions.assertThat(pjp.getArgs().length).isEqualTo(1);
        final String seite = CLIENT;
        final String call = pjp.toShortString();
        final Object send = pjp.getArgs()[0];
        String info = message(OUT, seite, call, send);
        if (info != null) {
            log.info(info);
        }
        final Object recv = pjp.proceed();
        info = message(IN, seite, call, recv);
        if (info != null) {
            log.info(info);
        }
        return recv;
    }

    @Around("@annotation(org.springframework.integration.annotation.ServiceActivator)")
    public Object service(final ProceedingJoinPoint pjp) throws Throwable {
        Assertions.assertThat(pjp.getArgs().length).isEqualTo(1);
        final String seite = SERVICE;
        final String call = pjp.toShortString();
        final Object recv = pjp.getArgs()[0];
        String info = message(IN, seite, call, recv);
        if (info != null) {
            log.info(info);
        }
        final Object send = pjp.proceed();
        info = message(OUT, seite, call, send);
        if (info != null) {
            log.info(info);
        }
        return send;
    }

    /**
     * Logging must happen in the calling method so that it is detected correctly by logback
     */
    private String message(final String direction, final String seite, final String call, final Object o) {
        StringBuilder info = null;
        if (log.isInfoEnabled() && o != null) {
            info = new StringBuilder(seite);
            info.append(" ");
            info.append(direction);
            info.append(" ");
            info.append(call);
            info.append(":\n");
            info.append(Strings.asStringReflectiveMultiline(o));
        }
        return Strings.asString(info);
    }

}
