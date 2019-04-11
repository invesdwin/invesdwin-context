package de.invesdwin.context.jfreechart.panel.helper.config.series.indicator;

import de.invesdwin.context.jfreechart.panel.InteractiveChartPanel;
import de.invesdwin.context.jfreechart.plot.dataset.IPlotSourceDataset;
import de.invesdwin.util.math.expression.IExpression;

public interface IIndicatorSeriesProvider {

    IIndicatorSeriesParameter[] NO_PARAMETERS = new IIndicatorSeriesParameter[0];

    String getName();

    String getDescription();

    String getExpressionName();

    default String getExpressionString(final IExpression[] args) {
        final StringBuilder sb = new StringBuilder(getExpressionName());
        if (args.length > 0) {
            sb.append("(");
            for (int i = 0; i < args.length; i++) {
                if (i > 0) {
                    sb.append(",");
                }
                sb.append(args[i].toString());
            }
            sb.append(")");
        }
        return sb.toString();
    }

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

    IPlotSourceDataset newInstance(InteractiveChartPanel chartPanel, IExpression[] args);

    void modifyDataset(InteractiveChartPanel chartPanel, IPlotSourceDataset dataset, IExpression[] args);

}
