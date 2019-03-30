package de.invesdwin.context.jfreechart.panel.helper.config.series;

import de.invesdwin.context.jfreechart.panel.InteractiveChartPanel;
import de.invesdwin.util.math.expression.IExpression;

public interface ISeriesProvider {

    String getName();

    String getDescription();

    ISeriesParameter[] getParameters();

    default IExpression[] getDefaultValues() {
        final ISeriesParameter[] parameters = getParameters();
        final IExpression[] defaultValues = new IExpression[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            defaultValues[i] = parameters[i].getDefaultValue();
        }
        return defaultValues;
    }

    String getPlotPaneId();

    void newInstance(InteractiveChartPanel chartPanel, IExpression[] args);

}
