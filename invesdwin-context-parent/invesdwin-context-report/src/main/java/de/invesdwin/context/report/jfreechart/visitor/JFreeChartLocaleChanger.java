package de.invesdwin.context.report.jfreechart.visitor;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import javax.annotation.concurrent.Immutable;

import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.SymbolAxis;
import org.jfree.chart.axis.ValueAxis;

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
    protected void processAxis(final ValueAxis axis) {
        super.processAxis(axis);
        if (axis instanceof DateAxis) {
            final DateAxis dateAxis = (DateAxis) axis;
            dateAxis.setLocale(locale);
        } else if (axis instanceof NumberAxis && !(axis instanceof SymbolAxis)) {
            final NumberAxis numberAxis = (NumberAxis) axis;
            if (numberAxis.getNumberFormatOverride() == null) {
                numberAxis.setNumberFormatOverride(
                        new DecimalFormat(Decimal.DEFAULT_DECIMAL_FORMAT, DecimalFormatSymbols.getInstance(locale)));
            }
        }
    }

}
