package de.invesdwin.context.beans;

import java.util.Set;

import javax.annotation.concurrent.ThreadSafe;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import de.invesdwin.util.lang.Strings;

@SuppressWarnings("serial")
@ThreadSafe
public class VerboseConstraintViolationException extends ConstraintViolationException {

    public VerboseConstraintViolationException(final Set<ConstraintViolation<?>> constraintViolations) {
        super(toString(constraintViolations), constraintViolations);
    }

    public VerboseConstraintViolationException(final ConstraintViolationException cause) {
        this(cause.getConstraintViolations());
        initCause(cause);
    }

    public static String toString(final Set<ConstraintViolation<?>> constraintViolations) {
        if (constraintViolations == null || constraintViolations.size() == 0) {
            return null;
        }
        final StringBuilder errors = new StringBuilder("Validation of [");
        errors.append(Strings.asStringReflective(constraintViolations.iterator().next().getRootBean()));
        errors.append("] failed:\n");
        for (final ConstraintViolation<?> constraintViolation : constraintViolations) {
            errors.append(constraintViolation.getRootBeanClass().getSimpleName());
            errors.append(".");
            errors.append(constraintViolation.getPropertyPath().toString());
            errors.append(": ");
            errors.append("[");
            errors.append(constraintViolation.getInvalidValue());
            errors.append("] ");
            errors.append(constraintViolation.getMessage());
            errors.append("\n");
        }
        return Strings.removeEnd(errors.toString(), "\n");
    }

}
