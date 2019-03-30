package de.invesdwin.context.jfreechart.panel.helper.config.indicator;

import de.invesdwin.context.jfreechart.panel.InteractiveChartPanel;
import de.invesdwin.util.math.expression.IExpression;

public interface IIndicatorSeriesProvider {

    String getName();

    String getDescription();

    IIndicatorSeriesParameter[] getParameters();

    default IExpression[] getDefaultValues() {
        final IIndicatorSeriesParameter[] parameters = getParameters();
        final IExpression[] defaultValues = new IExpression[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            defaultValues[i] = parameters[i].getDefaultValue();
        }
        return defaultValues;
    }

    String getPlotPaneId();

    void newInstance(InteractiveChartPanel chartPanel, IExpression[] args);

}
