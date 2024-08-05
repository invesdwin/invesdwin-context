package de.invesdwin.context.jfreechart.visitor;

import java.text.DecimalFormat;
import java.util.Locale;

import javax.annotation.concurrent.Immutable;

import org.jfree.chart.axis.Axis;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.SymbolAxis;

import de.invesdwin.context.jfreechart.axis.AxisType;
import de.invesdwin.context.jfreechart.axis.attached.IAttachedAxis;
import de.invesdwin.util.math.decimal.Decimal;

@Immutable
public class JFreeChartLocaleChanger extends AJFreeChartVisitor {

    public static final Locale DEFAULT_LOCALE = Locale.US;

    private final Locale locale;

    public JFreeChartLocaleChanger() {
        this(DEFAULT_LOCALE);
    }

    public JFreeChartLocaleChanger(final Locale locale) {
        this.locale = locale;
    }

    @Override
    public void processAxis(final Axis axis, final AxisType axisType) {
        super.processAxis(axis, axisType);
        changeLocale(axis, locale);
    }

    public static void changeLocale(final IAttachedAxis axis, final Locale locale) {
        changeLocale(axis.getAxis(), locale);
    }

    public static void changeLocale(final Axis axis, final Locale locale) {
        if (axis instanceof DateAxis) {
            final DateAxis dateAxis = (DateAxis) axis;
            dateAxis.setLocale(locale);
        } else if (axis instanceof NumberAxis && !(axis instanceof SymbolAxis)) {
            final NumberAxis numberAxis = (NumberAxis) axis;
            if (numberAxis.getNumberFormatOverride() == null) {
                final DecimalFormat formatter = Decimal.newDecimalFormatInstance(Decimal.DEFAULT_DECIMAL_FORMAT,
                        locale);
                numberAxis.setNumberFormatOverride(formatter);
            }
        }
    }

}
