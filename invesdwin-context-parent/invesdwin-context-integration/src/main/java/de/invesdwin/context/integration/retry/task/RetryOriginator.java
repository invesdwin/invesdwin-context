package de.invesdwin.context.integration.retry.task;

import javax.annotation.concurrent.Immutable;

import org.aspectj.lang.ProceedingJoinPoint;

import de.invesdwin.util.bean.AValueObject;

@Immutable
public class RetryOriginator extends AValueObject {

    public static final String ATTRIBUTE_RETRY_ORIGINATOR = "ATTRIBUTE_RETRY_ORIGINATOR";

    private final Class<?> type;
    private final String methodName;
    private final Object[] args;

    public RetryOriginator(final ProceedingJoinPoint pjp) {
        this(pjp.getSignature().getDeclaringType(), pjp.getSignature().getName(), pjp.getArgs());
    }

    public RetryOriginator(final Object obj, final String methodName, final Object... args) {
        this(obj.getClass(), methodName, args);
    }

    public RetryOriginator(final Class<?> clazz, final String methodName, final Object... args) {
        this.type = clazz;
        this.methodName = methodName;
        this.args = args;
    }

    public Class<?> getType() {
        return type;
    }

    public String getMethodName() {
        return methodName;
    }

    public Object[] getArgs() {
        return args;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(type.getName());
        sb.append(".");
        sb.append(methodName);
        sb.append("(");
        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                if (i > 0) {
                    sb.append(",");
                }
                sb.append(args[i]);
            }
        }
        sb.append(")");
        return sb.toString();
    }

}
