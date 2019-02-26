package de.invesdwin.context.jfreechart.plot.renderer.custom.order;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import javax.annotation.concurrent.NotThreadSafe;

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

@NotThreadSafe
public class CustomOrderPlottingRenderer extends AbstractXYItemRenderer
        implements ICustomRendererType, IUpDownColorRenderer {

    public static final Color UP_COLOR = CustomProfitLossRenderer.UP_COLOR;
    public static final Color DOWN_COLOR = CustomProfitLossRenderer.DOWN_COLOR;

    private static final LineStyleType LINE_STYLE_DEFAULT = LineStyleType.Solid;
    private static final LineStyleType LINE_STYLE_PENDING = LineStyleType.Dashed;

    private static final LineWidthType LINE_WIDTH_DEFAULT = LineWidthType._2;
    private static final LineWidthType LINE_WIDTH_TPSL = LineWidthType._1;

    private static final String SHAPE_CLOSED = "trend_line";
    private static final String SHAPE_ACTIVE = "horizontal_ray";

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

        //        final int lastItem = dataset.getItemCount(0) - 1;
        //        if (item == lastItem) {
        //            System.out.println("draw");
        //        }
    }

}
