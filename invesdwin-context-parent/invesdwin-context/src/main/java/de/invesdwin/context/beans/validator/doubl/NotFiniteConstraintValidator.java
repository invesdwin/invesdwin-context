package de.invesdwin.context.beans.validator.doubl;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.Doubles;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

@Immutable
public class NotFiniteConstraintValidator implements ConstraintValidator<NotFinite, Double> {

    @Override
    public boolean isValid(final Double value, final ConstraintValidatorContext context) {
        if (Doubles.isNaN(value)) {
            return true;
        }
        return Doubles.isNotFinite(value);
    }

}
