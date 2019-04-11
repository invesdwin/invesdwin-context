package de.invesdwin.context.jfreechart.panel.helper.config.series.expression;

import de.invesdwin.context.jfreechart.panel.InteractiveChartPanel;
import de.invesdwin.context.jfreechart.plot.dataset.IPlotSourceDataset;
import de.invesdwin.util.math.expression.IExpression;

public interface IExpressionSeriesProvider {

    IExpression newExpression(String arguments);

    String getPlotPaneId();

    IPlotSourceDataset newInstance(InteractiveChartPanel chartPanel, IExpression expression);

    void modifyDataset(InteractiveChartPanel chartPanel, IPlotSourceDataset dataset, IExpression expression);

}
