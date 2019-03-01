package de.invesdwin.context.jfreechart.plot.annotation;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.annotation.concurrent.NotThreadSafe;
import javax.swing.Icon;

import org.jfree.chart.annotations.AbstractXYAnnotation;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.XYAnnotationEntity;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.ui.RectangleAnchor;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.chart.util.Args;
import org.jfree.chart.util.PublicCloneable;
import org.jfree.chart.util.XYCoordinateType;
import org.jfree.data.Range;

@NotThreadSafe
public class XYIconAnnotation extends AbstractXYAnnotation implements Cloneable, PublicCloneable, Serializable {

    private static final long serialVersionUID = -4364694501921559958L;

    private final double x;

    private final double y;

    private transient Icon icon;

    private final RectangleAnchor anchor;

    private XYCoordinateType coordinateType;

    private XYAnnotationEntity entity;

    public XYIconAnnotation(final double x, final double y, final Icon icon) {
        this(x, y, icon, RectangleAnchor.CENTER);
    }

    public XYIconAnnotation(final double x, final double y, final Icon icon, final RectangleAnchor anchor) {
        super();
        Args.nullNotPermitted(icon, "image");
        Args.nullNotPermitted(anchor, "anchor");
        this.x = x;
        this.y = y;
        this.icon = icon;
        this.anchor = anchor;
        this.coordinateType = XYCoordinateType.RELATIVE;
    }

    public XYCoordinateType getCoordinateType() {
        return this.coordinateType;
    }

    public void setCoordinateType(final XYCoordinateType coordinateType) {
        this.coordinateType = coordinateType;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public Icon getIcon() {
        return this.icon;
    }

    public RectangleAnchor getImageAnchor() {
        return this.anchor;
    }

    @Override
    public void draw(final Graphics2D g2, final XYPlot plot, final Rectangle2D dataArea, final ValueAxis domainAxis,
            final ValueAxis rangeAxis, final int rendererIndex, final PlotRenderingInfo info) {

        final PlotOrientation orientation = plot.getOrientation();
        final AxisLocation domainAxisLocation = plot.getDomainAxisLocation();
        final AxisLocation rangeAxisLocation = plot.getRangeAxisLocation();
        final RectangleEdge domainEdge = Plot.resolveDomainAxisLocation(domainAxisLocation, orientation);
        final RectangleEdge rangeEdge = Plot.resolveRangeAxisLocation(rangeAxisLocation, orientation);

        final Range xRange = domainAxis.getRange();
        final Range yRange = rangeAxis.getRange();
        final double anchorX, anchorY;
        if (this.coordinateType == XYCoordinateType.RELATIVE) {
            anchorX = xRange.getLowerBound() + (modifyXInput(this.x) * xRange.getLength());
            anchorY = yRange.getLowerBound() + (modifyYInput(this.y) * yRange.getLength());
        } else {
            anchorX = modifyXInput(this.x);
            anchorY = modifyYInput(this.y);
        }

        final float j2DX = (float) domainAxis.valueToJava2D(anchorX, dataArea, domainEdge);
        final float j2DY = (float) rangeAxis.valueToJava2D(anchorY, dataArea, rangeEdge);
        float xx = 0.0f;
        float yy = 0.0f;
        if (orientation == PlotOrientation.HORIZONTAL) {
            xx = j2DY;
            yy = j2DX;
        } else if (orientation == PlotOrientation.VERTICAL) {
            xx = j2DX;
            yy = j2DY;
        }
        final int w = this.icon.getIconWidth();
        final int h = this.icon.getIconHeight();

        final Rectangle2D imageRect = new Rectangle2D.Double(0, 0, w, h);
        final Point2D anchorPoint = this.anchor.getAnchorPoint(imageRect);
        xx = xx - (float) anchorPoint.getX();
        yy = yy - (float) anchorPoint.getY();
        xx = modifyXOutput(xx);
        yy = modifyYOutput(yy);
        icon.paintIcon(null, g2, (int) xx, (int) yy);

        final String toolTip = getToolTipText();
        final String url = getURL();
        addEntity(info, new Rectangle2D.Float(xx, yy, w, h), rendererIndex, toolTip, url);
    }

    protected double modifyXInput(final double x) {
        return x;
    }

    protected double modifyYInput(final double y) {
        return y;
    }

    protected float modifyXOutput(final float x) {
        return x;
    }

    protected float modifyYOutput(final float y) {
        return y;
    }

    @Override
    protected void addEntity(final PlotRenderingInfo info, final Shape hotspot, final int rendererIndex,
            final String toolTipText, final String urlText) {
        if (info == null) {
            return;
        }
        final EntityCollection entities = info.getOwner().getEntityCollection();
        if (entities == null) {
            return;
        }
        final XYAnnotationEntity entity = new XYIconAnnotationEntity(this, hotspot, rendererIndex, toolTipText,
                urlText);
        entities.add(entity);
        this.entity = entity;
    }

    public XYAnnotationEntity getEntity() {
        return entity;
    }

    public void setEntity(final XYAnnotationEntity entity) {
        this.entity = entity;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        // now try to reject equality...
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof XYIconAnnotation)) {
            return false;
        }
        final XYIconAnnotation that = (XYIconAnnotation) obj;
        if (this.x != that.x) {
            return false;
        }
        if (this.y != that.y) {
            return false;
        }
        if (!org.jfree.chart.util.ObjectUtils.equal(this.icon, that.icon)) {
            return false;
        }
        if (!this.anchor.equals(that.anchor)) {
            return false;
        }
        // seems to be the same...
        return true;
    }

    @Override
    public int hashCode() {
        return this.icon.hashCode();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    private void writeObject(final ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        //SerialUtils.writeImage(this.image, stream);
    }

    private void readObject(final ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        //this.image = SerialUtils.readImage(stream);
    }

}
