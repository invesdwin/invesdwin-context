package de.invesdwin.context.beans.validator.doubl;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.Doubles;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

@Immutable
public class DoubleMaxValidator implements ConstraintValidator<DoubleMax, Number> {

    private boolean inclusive;
    private double max;

    @Override
    public void initialize(final DoubleMax constraintAnnotation) {
        this.inclusive = constraintAnnotation.inclusive();
        this.max = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(final Number value, final ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        final double doubleValue = value.doubleValue();
        if (Doubles.isNaN(doubleValue)) {
            return true;
        }
        if (inclusive) {
            return Doubles.isLessThanOrEqualTo(doubleValue, max);
        } else {
            return Doubles.isLessThan(doubleValue, max);
        }
    }

}
