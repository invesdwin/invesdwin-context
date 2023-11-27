package de.invesdwin.context.beans.validator.doubl;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.Doubles;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

@Immutable
public class NotInfiniteValidator implements ConstraintValidator<NotInfinite, Number> {

    @Override
    public boolean isValid(final Number value, final ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        final double doubleValue = value.doubleValue();
        if (Doubles.isNaN(doubleValue)) {
            return true;
        }
        return Doubles.isNotInfinite(doubleValue);
    }

}
