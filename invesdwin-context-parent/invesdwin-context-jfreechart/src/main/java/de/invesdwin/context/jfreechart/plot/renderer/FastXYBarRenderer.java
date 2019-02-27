package de.invesdwin.context.jfreechart.plot.renderer;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import javax.annotation.concurrent.NotThreadSafe;

import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.data.xy.XYDataset;

import de.invesdwin.context.jfreechart.plot.annotation.priceline.IDelegatePriceLineRenderer;
import de.invesdwin.context.jfreechart.plot.annotation.priceline.IPriceLineRenderer;
import de.invesdwin.context.jfreechart.plot.annotation.priceline.XYPriceLineAnnotation;

@NotThreadSafe
public class FastXYBarRenderer extends XYBarRenderer implements IDelegatePriceLineRenderer {

    private final XYPriceLineAnnotation priceLineAnnotation;

    public FastXYBarRenderer(final XYDataset dataset) {
        this(dataset, 0D);
    }

    public FastXYBarRenderer(final XYDataset dataset, final double margin) {
        super(margin);
        this.priceLineAnnotation = new XYPriceLineAnnotation(dataset, this);
        addAnnotation(priceLineAnnotation);
    }

    @Override
    public IPriceLineRenderer getDelegatePriceLineRenderer() {
        return priceLineAnnotation;
    }

    @Override
    public XYItemRendererState initialise(final Graphics2D g2, final Rectangle2D dataArea, final XYPlot plot,
            final XYDataset dataset, final PlotRenderingInfo info) {
        //info null to skip EntityCollection stuff
        return super.initialise(g2, dataArea, plot, dataset, null);
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
