package de.invesdwin.context.beans.validator.doubl;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.Doubles;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

@Immutable
public class DoubleMinValidator implements ConstraintValidator<DoubleMin, Number> {

    private boolean inclusive;
    private double min;

    @Override
    public void initialize(final DoubleMin constraintAnnotation) {
        this.inclusive = constraintAnnotation.inclusive();
        this.min = constraintAnnotation.value();
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
            return Doubles.isGreaterThanOrEqualTo(doubleValue, min);
        } else {
            return Doubles.isGreaterThan(doubleValue, min);
        }
    }

}
