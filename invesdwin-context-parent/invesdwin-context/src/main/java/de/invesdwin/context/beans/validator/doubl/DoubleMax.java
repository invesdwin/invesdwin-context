package de.invesdwin.context.beans.validator.doubl;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * The annotated element has to be in the appropriate range. Apply on numeric values or string representation of the
 * numeric value. NaN is valid (use @NotNan to disallow).
 */
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
@Retention(RUNTIME)
@Repeatable(DoubleMax.List.class)
@Documented
@Constraint(validatedBy = { DoubleMaxValidator.class })
public @interface DoubleMax {

    String message() default "{jakarta.validation.constraints.DecimalMax.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    double value();

    boolean inclusive() default true;

    @Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
    @Retention(RUNTIME)
    @Documented
    @interface List {

        DoubleMax[] value();
    }
}
