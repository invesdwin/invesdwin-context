package de.invesdwin.context.jfreechart.plot.renderer;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import javax.annotation.concurrent.NotThreadSafe;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.data.xy.XYDataset;

import de.invesdwin.context.jfreechart.plot.annotation.priceline.IDelegatePriceLineXYItemRenderer;
import de.invesdwin.context.jfreechart.plot.annotation.priceline.IPriceLineRenderer;
import de.invesdwin.context.jfreechart.plot.annotation.priceline.XYPriceLineAnnotation;

@NotThreadSafe
public class FastStandardXYItemRenderer extends StandardXYItemRenderer implements IDelegatePriceLineXYItemRenderer {

    private final XYPriceLineAnnotation priceLineAnnotation;

    public FastStandardXYItemRenderer(final XYDataset dataset) {
        this.priceLineAnnotation = new XYPriceLineAnnotation(dataset, this);
        addAnnotation(priceLineAnnotation);
    }

    @Override
    public IPriceLineRenderer getDelegatePriceLineRenderer() {
        return priceLineAnnotation;
    }

    //CHECKSTYLE:OFF
    @Override
    public void drawItem(final Graphics2D g2, final XYItemRendererState state, final Rectangle2D dataArea,
            final PlotRenderingInfo info, final XYPlot plot, final ValueAxis domainAxis, final ValueAxis rangeAxis,
            final XYDataset dataset, final int series, final int item, final CrosshairState crosshairState,
            final int pass) {
        //CHECKSTYLE:ON
        //info null to skip entity collection stuff
        super.drawItem(g2, state, dataArea, null, plot, domainAxis, rangeAxis, dataset, series, item, crosshairState,
                pass);
    }

    @Override
    protected void updateCrosshairValues(final CrosshairState crosshairState, final double x, final double y,
            final int datasetIndex, final double transX, final double transY, final PlotOrientation orientation) {
        //noop
    }

    @Override
    protected void addEntity(final EntityCollection entities, final Shape hotspot, final XYDataset dataset,
            final int series, final int item, final double entityX, final double entityY) {
        //noop
    }

}
