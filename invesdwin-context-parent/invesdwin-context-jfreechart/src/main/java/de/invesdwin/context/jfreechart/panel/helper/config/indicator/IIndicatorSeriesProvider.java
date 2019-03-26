package de.invesdwin.context.jfreechart.panel.helper.config.indicator;

import de.invesdwin.context.jfreechart.plot.annotation.priceline.IPriceLineXYItemRenderer;
import de.invesdwin.context.jfreechart.plot.dataset.IPlotSourceDataset;
import de.invesdwin.util.math.expression.IExpression;

public interface IIndicatorSeriesProvider {

    String getName();

    String getDescription();

    IIndicatorSeriesParameter[] getParameters();

    String getPlotPaneId();

    IPlotSourceDataset newDataset(IExpression[] args);

    IPriceLineXYItemRenderer newRenderer(IPlotSourceDataset dataset);

}
