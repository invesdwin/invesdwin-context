package de.invesdwin.context.jfreechart.panel.helper.config.indicator;

import de.invesdwin.context.jfreechart.panel.InteractiveChartPanel;
import de.invesdwin.util.math.expression.IExpression;

public interface IIndicatorSeriesProvider {

    String getName();

    String getDescription();

    IIndicatorSeriesParameter[] getParameters();

    String getPlotPaneId();

    void newInstance(InteractiveChartPanel chartPanel, IExpression[] args);

}
