package de.invesdwin.context.jfreechart.plot.renderer.custom.order;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.util.NoSuchElementException;

import javax.annotation.concurrent.NotThreadSafe;

import org.jfree.chart.annotations.XYLineAnnotation;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.AbstractXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.data.xy.XYDataset;

import de.invesdwin.context.jfreechart.panel.helper.config.LineStyleType;
import de.invesdwin.context.jfreechart.panel.helper.config.LineWidthType;
import de.invesdwin.context.jfreechart.panel.helper.config.PlotConfigurationHelper;
import de.invesdwin.context.jfreechart.panel.helper.config.PriceInitialSettings;
import de.invesdwin.context.jfreechart.plot.renderer.IUpDownColorRenderer;
import de.invesdwin.context.jfreechart.plot.renderer.custom.CustomProfitLossRenderer;
import de.invesdwin.context.jfreechart.plot.renderer.custom.ICustomRendererType;
import de.invesdwin.util.collections.iterable.ICloseableIterator;

@NotThreadSafe
public class CustomOrderPlottingRenderer extends AbstractXYItemRenderer
        implements ICustomRendererType, IUpDownColorRenderer {

    public static final Color UP_COLOR = CustomProfitLossRenderer.UP_COLOR;
    public static final Color DOWN_COLOR = CustomProfitLossRenderer.DOWN_COLOR;

    private static final LineStyleType LINE_STYLE_DEFAULT = LineStyleType.Solid;
    private static final LineStyleType LINE_STYLE_PENDING = LineStyleType.Dashed;

    private static final LineWidthType LINE_WIDTH_DEFAULT = LineWidthType._2;
    private static final LineWidthType LINE_WIDTH_TPSL = LineWidthType._1;

    private Color upColor;
    private Color downColor;

    public CustomOrderPlottingRenderer(final PlotConfigurationHelper plotConfigurationHelper) {
        final PriceInitialSettings config = plotConfigurationHelper.getPriceInitialSettings();
        setSeriesPaint(0, UP_COLOR);
        setSeriesStroke(0, config.getSeriesStroke());
        upColor = UP_COLOR;
        downColor = DOWN_COLOR;
    }

    @Override
    public boolean isLineStyleConfigurable() {
        return false;
    }

    @Override
    public boolean isLineWidthConfigurable() {
        return false;
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

                    final LineWidthType lineWidth;
                    if (next.isTpsl()) {
                        lineWidth = LINE_WIDTH_TPSL;
                    } else {
                        lineWidth = LINE_WIDTH_DEFAULT;
                    }
                    final Stroke stroke = lineStyle.getStroke(lineWidth);

                    final int closeTimeIndex;
                    if (next.isClosed()) {
                        closeTimeIndex = next.getCloseTimeIndex();
                    } else {
                        closeTimeIndex = ((int) domainAxis.getUpperBound()) + 1;
                    }

                    final XYLineAnnotation line = new XYLineAnnotation(next.getOpenTimeIndex(), next.getOpenPrice(),
                            closeTimeIndex, next.getClosePrice(), stroke, color);
                    final int index = getPlot().getIndexOf(this);
                    line.draw(g2, plot, dataArea, domainAxis, rangeAxis, index, info);
                }
            } catch (final NoSuchElementException e) {
                //end reached
            }
        }
    }

}
