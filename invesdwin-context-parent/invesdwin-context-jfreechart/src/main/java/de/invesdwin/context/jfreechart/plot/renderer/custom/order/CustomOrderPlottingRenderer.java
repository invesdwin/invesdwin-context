package de.invesdwin.context.jfreechart.plot.renderer.custom.order;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.text.NumberFormat;
import java.util.NoSuchElementException;

import javax.annotation.concurrent.NotThreadSafe;

import org.jfree.chart.annotations.XYLineAnnotation;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.AbstractXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.chart.ui.TextAnchor;
import org.jfree.data.xy.XYDataset;

import de.invesdwin.context.jfreechart.panel.helper.config.LineStyleType;
import de.invesdwin.context.jfreechart.panel.helper.config.LineWidthType;
import de.invesdwin.context.jfreechart.panel.helper.config.PlotConfigurationHelper;
import de.invesdwin.context.jfreechart.panel.helper.config.PriceInitialSettings;
import de.invesdwin.context.jfreechart.plot.XYPlots;
import de.invesdwin.context.jfreechart.plot.renderer.IUpDownColorRenderer;
import de.invesdwin.context.jfreechart.plot.renderer.custom.CustomProfitLossRenderer;
import de.invesdwin.context.jfreechart.plot.renderer.custom.ICustomRendererType;
import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.lang.Strings;

@NotThreadSafe
public class CustomOrderPlottingRenderer extends AbstractXYItemRenderer
        implements ICustomRendererType, IUpDownColorRenderer {

    public static final Color UP_COLOR = CustomProfitLossRenderer.UP_COLOR;
    public static final Color DOWN_COLOR = CustomProfitLossRenderer.DOWN_COLOR;
    private static final ValueAxis ABSOLUTE_AXIS = XYPlots.DRAWING_ABSOLUTE_AXIS;

    private static final Font FONT = new Font("Verdana", Font.PLAIN, 9);

    private static final LineStyleType LINE_STYLE_DEFAULT = LineStyleType.Solid;
    private static final LineStyleType LINE_STYLE_PENDING = LineStyleType.Dashed;

    private final OrderPlottingDataset dataset;
    private Color upColor;
    private Color downColor;

    public CustomOrderPlottingRenderer(final PlotConfigurationHelper plotConfigurationHelper,
            final OrderPlottingDataset dataset) {
        this.dataset = dataset;
        final PriceInitialSettings config = plotConfigurationHelper.getPriceInitialSettings();
        setSeriesPaint(0, UP_COLOR);
        setSeriesStroke(0, config.getSeriesStroke());
        upColor = UP_COLOR;
        downColor = DOWN_COLOR;
    }

    @Override
    public Paint getItemPaint(final int row, final int column) {
        if (dataset.isLastTradeProfit()) {
            return upColor;
        } else {
            return downColor;
        }
    }

    @Override
    public boolean isLineStyleConfigurable() {
        return false;
    }

    @Override
    public boolean isLineWidthConfigurable() {
        return true;
    }

    @Override
    public boolean isSeriesColorConfigurable() {
        return false;
    }

    @Override
    public String getName() {
        return "Order Plotting";
    }

    @Override
    public void setUpColor(final Color upColor) {
        this.upColor = upColor;
    }

    @Override
    public Color getUpColor() {
        return upColor;
    }

    @Override
    public void setDownColor(final Color downColor) {
        this.downColor = downColor;
    }

    @Override
    public Color getDownColor() {
        return downColor;
    }

    @Override
    public boolean isSeriesRendererTypeConfigurable() {
        return false;
    }

    @Override
    public String getUpColorName() {
        return "Profit";
    }

    @Override
    public String getDownColorName() {
        return "Loss";
    }

    //CHECKSTYLE:OFF
    @Override
    public void drawItem(final Graphics2D g2, final XYItemRendererState state, final Rectangle2D dataArea,
            final PlotRenderingInfo info, final XYPlot plot, final ValueAxis domainAxis, final ValueAxis rangeAxis,
            final XYDataset dataset, final int series, final int item, final CrosshairState crosshairState,
            final int pass) {
        //CHECKSTYLE:ON
        final int lastItem = state.getLastItemIndex();
        if (item == lastItem) {
            final OrderPlottingDataset cDataset = (OrderPlottingDataset) dataset;
            final int firstItem = state.getFirstItemIndex();
            final int rendererIndex = getPlot().getIndexOf(this);
            final NumberAxis cRangeAxis = (NumberAxis) rangeAxis;
            final NumberFormat rangeAxisFormat = cRangeAxis.getNumberFormatOverride();
            final PlotOrientation orientation = plot.getOrientation();
            final RectangleEdge domainEdge = Plot.resolveDomainAxisLocation(plot.getDomainAxisLocation(), orientation);
            final RectangleEdge rangeEdge = Plot.resolveRangeAxisLocation(plot.getRangeAxisLocation(), orientation);
            final LineWidthType lineWidth = LineWidthType.valueOf(getSeriesStroke(series));
            final ICloseableIterator<OrderPlottingDataItem> visibleItems = cDataset.getVisibleItems(firstItem, lastItem)
                    .iterator();
            try {
                while (true) {
                    final OrderPlottingDataItem next = visibleItems.next();

                    final Color color;
                    if (next.isProfit()) {
                        color = upColor;
                    } else {
                        color = downColor;
                    }

                    final LineStyleType lineStyle;
                    if (next.isPending()) {
                        lineStyle = LINE_STYLE_PENDING;
                    } else {
                        lineStyle = LINE_STYLE_DEFAULT;
                    }

                    final Stroke stroke = lineStyle.getStroke(lineWidth);

                    final boolean closed = next.isClosed();
                    final double x1 = domainAxis.valueToJava2D(next.getOpenTimeIndex(), dataArea, domainEdge);
                    final double x2;
                    if (closed) {
                        x2 = domainAxis.valueToJava2D(next.getCloseTimeIndex(), dataArea, domainEdge);
                    } else {
                        x2 = dataArea.getMaxX();
                    }

                    final double openPrice = next.getOpenPrice();
                    final double closePrice = next.getClosePrice();

                    final double y1 = rangeAxis.valueToJava2D(openPrice, dataArea, rangeEdge);
                    final double y2 = rangeAxis.valueToJava2D(closePrice, dataArea, rangeEdge);
                    final XYLineAnnotation lineAnnotation = new XYLineAnnotation(x1, y1, x2, y2, stroke, color);
                    lineAnnotation.draw(g2, plot, dataArea, ABSOLUTE_AXIS, ABSOLUTE_AXIS, rendererIndex, info);

                    if (!closed) {
                        final String label = next.getLabel();
                        if (Strings.isNotBlank(label)) {
                            final XYTextAnnotation labelAnnotation = new XYTextAnnotation(label + " ", x2, y2 - 1D);
                            labelAnnotation.setPaint(color);
                            labelAnnotation.setFont(FONT);
                            labelAnnotation.setTextAnchor(TextAnchor.BOTTOM_RIGHT);
                            labelAnnotation.draw(g2, plot, dataArea, ABSOLUTE_AXIS, ABSOLUTE_AXIS, rendererIndex, info);
                            final XYTextAnnotation priceAnnotation = new XYTextAnnotation(
                                    rangeAxisFormat.format(closePrice) + " ", x2, y2 + 1D);
                            priceAnnotation.setPaint(color);
                            priceAnnotation.setFont(FONT);
                            priceAnnotation.setTextAnchor(TextAnchor.TOP_RIGHT);
                            priceAnnotation.draw(g2, plot, dataArea, ABSOLUTE_AXIS, ABSOLUTE_AXIS, rendererIndex, info);
                        }
                    }
                }
            } catch (final NoSuchElementException e) {
                //end reached
            }
        }
    }

}
