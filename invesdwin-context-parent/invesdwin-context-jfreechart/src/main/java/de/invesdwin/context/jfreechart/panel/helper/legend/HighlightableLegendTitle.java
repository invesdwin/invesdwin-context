package de.invesdwin.context.jfreechart.panel.helper.legend;

import java.awt.Font;
import java.awt.Paint;
import java.text.NumberFormat;

import javax.annotation.concurrent.NotThreadSafe;

import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemSource;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.general.Dataset;
import org.jfree.data.xy.OHLCDataset;
import org.jfree.data.xy.XYDataset;

import de.invesdwin.context.jfreechart.panel.InteractiveChartPanel;
import de.invesdwin.context.jfreechart.plot.XYPlots;
import de.invesdwin.context.jfreechart.plot.dataset.IPlotSourceDataset;
import de.invesdwin.util.error.UnknownArgumentException;

@NotThreadSafe
public class HighlightableLegendTitle extends CustomLegendTitle {

    private static final Font LEGEND_FONT = XYPlots.DEFAULT_FONT;
    private static final Font HIGHLIGHTED_LEGEND_FONT = LEGEND_FONT.deriveFont(Font.BOLD);

    private final InteractiveChartPanel chartPanel;

    public HighlightableLegendTitle(final InteractiveChartPanel chartPanel, final LegendItemSource source) {
        super(source);
        this.chartPanel = chartPanel;
    }

    @Override
    protected String newLabel(final LegendItem item) {
        int domainMarkerItem = (int) chartPanel.getPlotCrosshairHelper().getDomainCrosshairMarker().getValue();
        if (domainMarkerItem == -1) {
            domainMarkerItem = chartPanel.getDataset().getData().size() - 1;
        }
        if (domainMarkerItem >= 0) {
            final IPlotSourceDataset dataset = (IPlotSourceDataset) item.getDataset();
            final String label = dataset.getSeriesTitle();
            final int series = item.getSeriesIndex();
            final int lastItem = dataset.getItemCount(series) - 1;
            if (domainMarkerItem > lastItem) {
                domainMarkerItem = lastItem;
            }
            if (!dataset.isLegendValueVisible(series, domainMarkerItem)) {
                return label;
            }
            final XYPlot plot = dataset.getPlot();
            if (plot == chartPanel.getCombinedPlot().getTrashPlot()) {
                return label;
            }
            final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxisForDataset(item.getDatasetIndex());
            final NumberFormat rangeAxisFormat = rangeAxis.getNumberFormatOverride();
            if (dataset instanceof OHLCDataset) {
                final OHLCDataset ohlc = (OHLCDataset) dataset;
                final StringBuilder sb = new StringBuilder(label);
                sb.append(" O:");
                sb.append(rangeAxisFormat.format(ohlc.getOpen(series, domainMarkerItem)));
                sb.append(" H:");
                sb.append(rangeAxisFormat.format(ohlc.getHigh(series, domainMarkerItem)));
                sb.append(" L:");
                sb.append(rangeAxisFormat.format(ohlc.getLow(series, domainMarkerItem)));
                sb.append(" C:");
                sb.append(rangeAxisFormat.format(ohlc.getClose(series, domainMarkerItem)));
                sb.append(" T:");
                sb.append(chartPanel.getDomainAxisFormat().format(domainMarkerItem));
                return sb.toString();
            } else {
                final StringBuilder sb = new StringBuilder(label);
                sb.append(" ");
                sb.append(rangeAxisFormat.format(dataset.getYValue(series, domainMarkerItem)));
                return sb.toString();
            }
        } else {
            return super.newLabel(item);
        }
    }

    @Override
    protected Font newTextFont(final LegendItem item, final Font textFont) {
        final HighlightedLegendInfo highlightedLegendInfo = chartPanel.getPlotLegendHelper().getHighlightedLegendInfo();
        if (highlightedLegendInfo != null && highlightedLegendInfo.getDatasetIndex() == item.getDatasetIndex()) {
            final IPlotSourceDataset plotSource = (IPlotSourceDataset) item.getDataset();
            if (highlightedLegendInfo.getPlot() == plotSource.getPlot()) {
                return HIGHLIGHTED_LEGEND_FONT;
            }
        }
        return LEGEND_FONT;
    }

    @Override
    protected Paint newFillPaint(final LegendItem item) {
        int domainMarkerItem = (int) chartPanel.getPlotCrosshairHelper().getDomainCrosshairMarker().getValue();
        if (domainMarkerItem == -1) {
            domainMarkerItem = chartPanel.getDataset().getData().size() - 1;
        }
        if (domainMarkerItem >= 0) {
            final Dataset dataset = item.getDataset();
            final IPlotSourceDataset plotSource = (IPlotSourceDataset) dataset;
            final int datasetIndex = item.getDatasetIndex();
            final XYItemRenderer renderer = plotSource.getPlot().getRenderer(datasetIndex);
            if (renderer == null) {
                return super.newFillPaint(item);
            } else if (dataset instanceof OHLCDataset) {
                final OHLCDataset ohlc = (OHLCDataset) dataset;
                if (domainMarkerItem >= ohlc.getItemCount(0)) {
                    domainMarkerItem = ohlc.getItemCount(0) - 1;
                }
                return renderer.getItemPaint(0, domainMarkerItem);
            } else if (dataset instanceof XYDataset) {
                final XYDataset xy = (XYDataset) dataset;
                if (domainMarkerItem >= xy.getItemCount(0)) {
                    domainMarkerItem = xy.getItemCount(0) - 1;
                }
                return renderer.getItemPaint(0, domainMarkerItem);
            } else {
                throw UnknownArgumentException.newInstance(Class.class, dataset.getClass());
            }
        } else {
            return super.newFillPaint(item);
        }
    }
}