package de.invesdwin.context.beans.validator;

import java.util.Locale;
import java.util.Set;

import javax.annotation.concurrent.ThreadSafe;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.validation.Errors;

import de.invesdwin.context.beans.VerboseConstraintViolationException;
import jakarta.inject.Named;
import jakarta.validation.Configuration;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.MessageInterpolator;
import jakarta.validation.Validation;
import jakarta.validation.bootstrap.GenericBootstrap;
import jakarta.validation.executable.ExecutableValidator;
import jakarta.validation.metadata.BeanDescriptor;

@Named
@ThreadSafe
public class BeanValidator
        implements org.springframework.validation.Validator, jakarta.validation.Validator, FactoryBean<BeanValidator> {

    private static final BeanValidator INSTANCE = new BeanValidator();
    private final jakarta.validation.Validator delegate;

    public BeanValidator() {
        final GenericBootstrap bootstrap = Validation.byDefaultProvider();
        final Configuration<?> configuration = bootstrap.configure();
        final MessageInterpolator defaultMessageInterpolator = configuration.getDefaultMessageInterpolator();
        this.delegate = Validation.buildDefaultValidatorFactory()
                .usingContext()
                .messageInterpolator(new LocaleContextMessageInterpolator(defaultMessageInterpolator))
                .getValidator();
    }

    public static BeanValidator getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean supports(final Class<?> clazz) {
        return true;
    }

    @Override
    public void validate(final Object target, final Errors errors) {
        final Set<ConstraintViolation<Object>> constraintViolations = delegate.validate(target);
        for (final ConstraintViolation<Object> constraintViolation : constraintViolations) {
            final String propertyPath = constraintViolation.getPropertyPath().toString();
            final String message = constraintViolation.getMessage();
            errors.rejectValue(propertyPath, "", message);
        }
    }

    public boolean isValid(final Object target) {
        return delegate.validate(target).isEmpty();
    }

    public ConstraintViolationException validate(final Object target, final Locale locale) {
        LocaleContextHolder.setLocale(locale);
        try {
            return validate(target);
        } finally {
            LocaleContextHolder.setLocale(null);
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public ConstraintViolationException validate(final Object target) {
        final Set<ConstraintViolation<?>> constraintViolations = (Set) delegate.validate(target);
        if (!constraintViolations.isEmpty()) {
            return new VerboseConstraintViolationException(constraintViolations);
        } else {
            return null;
        }
    }

    public void validateThrow(final Object target) {
        final ConstraintViolationException error = validate(target);
        if (error != null) {
            throw error;
        }
    }

    @Override
    public <T> Set<ConstraintViolation<T>> validate(final T object, final Class<?>... groups) {
        return delegate.validate(object, groups);
    }

    @Override
    public <T> Set<ConstraintViolation<T>> validateProperty(final T object, final String propertyName,
            final Class<?>... groups) {
        return delegate.validateProperty(object, propertyName, groups);
    }

    @Override
    public <T> Set<ConstraintViolation<T>> validateValue(final Class<T> beanType, final String propertyName,
            final Object value, final Class<?>... groups) {
        return delegate.validateValue(beanType, propertyName, value, groups);
    }

    @Override
    public BeanDescriptor getConstraintsForClass(final Class<?> clazz) {
        return delegate.getConstraintsForClass(clazz);
    }

    @Override
    public <T> T unwrap(final Class<T> type) {
        return delegate.unwrap(type);
    }

    @Override
    public BeanValidator getObject() throws Exception {
        return getInstance();
    }

    @Override
    public Class<?> getObjectType() {
        return getInstance().getClass();
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public ExecutableValidator forExecutables() {
        return delegate.forExecutables();
    }

}
