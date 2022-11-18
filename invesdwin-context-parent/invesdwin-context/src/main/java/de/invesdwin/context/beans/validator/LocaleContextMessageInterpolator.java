package de.invesdwin.context.beans.validator;

import java.util.Locale;

import javax.annotation.concurrent.Immutable;

import org.springframework.context.i18n.LocaleContextHolder;

import de.invesdwin.util.assertions.Assertions;
import jakarta.validation.MessageInterpolator;

/**
 * Delegates to a target {@link MessageInterpolator} implementation but enforces Spring's managed Locale. Typically used
 * to wrap the validation provider's default interpolator.
 *
 * @author Juergen Hoeller
 * @since 3.0
 * @see org.springframework.context.i18n.LocaleContextHolder#getLocale()
 */
@Immutable
public class LocaleContextMessageInterpolator implements MessageInterpolator {

    private final MessageInterpolator targetInterpolator;

    /**
     * Create a new LocaleContextMessageInterpolator, wrapping the given target interpolator.
     *
     * @param targetInterpolator
     *            the target MessageInterpolator to wrap
     */
    public LocaleContextMessageInterpolator(final MessageInterpolator targetInterpolator) {
        Assertions.checkNotNull(targetInterpolator, "Target MessageInterpolator must not be null");
        this.targetInterpolator = targetInterpolator;
    }

    @Override
    public String interpolate(final String message, final Context context) {
        return this.targetInterpolator.interpolate(message, context, LocaleContextHolder.getLocale());
    }

    @Override
    public String interpolate(final String message, final Context context, final Locale locale) {
        return this.targetInterpolator.interpolate(message, context, locale);
    }

}
