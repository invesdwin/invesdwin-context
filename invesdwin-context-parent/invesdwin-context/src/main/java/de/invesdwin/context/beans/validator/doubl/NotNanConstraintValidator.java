package de.invesdwin.context.beans.validator.doubl;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.Doubles;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

@Immutable
public class NotNanConstraintValidator implements ConstraintValidator<NotNan, Double> {

    @Override
    public boolean isValid(final Double value, final ConstraintValidatorContext context) {
        return !Doubles.isNaN(value);
    }

}
