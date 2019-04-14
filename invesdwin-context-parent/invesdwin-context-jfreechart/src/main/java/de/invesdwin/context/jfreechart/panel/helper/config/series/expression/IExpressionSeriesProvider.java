package de.invesdwin.context.jfreechart.panel.helper.config.series.expression;

import de.invesdwin.context.jfreechart.panel.InteractiveChartPanel;
import de.invesdwin.context.jfreechart.plot.dataset.IPlotSourceDataset;
import de.invesdwin.util.math.expression.IExpression;

public interface IExpressionSeriesProvider {

    String getPlotPaneId();

    IExpression parseExpression(String expression);

    IPlotSourceDataset newInstance(InteractiveChartPanel chartPanel, String expression);

    void modifyDataset(InteractiveChartPanel chartPanel, IPlotSourceDataset dataset, String expression);

}
