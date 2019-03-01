package de.invesdwin.context.jfreechart.plot.annotation;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.annotation.concurrent.NotThreadSafe;

import org.jfree.chart.annotations.AbstractXYAnnotation;
import org.jfree.chart.annotations.XYLineAnnotation;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.block.BlockParams;
import org.jfree.chart.block.EntityBlockResult;
import org.jfree.chart.block.RectangleConstraint;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.XYAnnotationEntity;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.ui.HorizontalAlignment;
import org.jfree.chart.ui.RectangleAnchor;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.chart.ui.Size2D;
import org.jfree.data.Range;

import de.invesdwin.context.jfreechart.panel.helper.config.LineStyleType;
import de.invesdwin.context.jfreechart.panel.helper.config.LineWidthType;
import de.invesdwin.context.jfreechart.plot.XYPlots;
import de.invesdwin.util.lang.Colors;
import de.invesdwin.util.math.decimal.scaled.Percent;
import de.invesdwin.util.math.decimal.scaled.PercentScale;

@NotThreadSafe
public class XYNoteAnnotation extends AbstractXYAnnotation {

    public static final Percent NOTE_TRANSPARENCY = new Percent(15, PercentScale.PERCENT);
    public static final Color NOTE_BACKGROUND_COLOR = Colors.setTransparency(Color.WHITE, NOTE_TRANSPARENCY);
    public static final Color NOTE_BORDER_COLOR = Colors.setTransparency(Color.BLACK,
            new Percent(60, PercentScale.PERCENT));
    public static final Stroke NOTE_STROKE = LineStyleType.Solid.getStroke(LineWidthType._1);
    public static final Font DEFAULT_FONT = XYPlots.DEFAULT_FONT;

    private final XYNoteIconAnnotation noteIconAnnotation;
    private final TextTitle title;

    private XYAnnotationEntity entity;

    public XYNoteAnnotation(final XYNoteIconAnnotation noteIconAnnotation) {
        this.noteIconAnnotation = noteIconAnnotation;
        this.title = newNoteTitle();
    }

    public static TextTitle newNoteTitle() {
        final TextTitle textTitle = new TextTitle("", DEFAULT_FONT);
        textTitle.setBackgroundPaint(NOTE_BACKGROUND_COLOR);
        textTitle.setTextAlignment(HorizontalAlignment.LEFT);
        textTitle.setFrame(new BlockBorder(1D, 0D, 0D, 0D, NOTE_BORDER_COLOR));
        return textTitle;
    }

    public TextTitle getTitle() {
        return this.title;
    }

    //CHECKSTYLE:OFF
    @Override
    public void draw(final Graphics2D g2, final XYPlot plot, final Rectangle2D dataArea, final ValueAxis domainAxis,
            final ValueAxis rangeAxis, final int rendererIndex, final PlotRenderingInfo info) {
        //CHECKSTYLE:ON

        final PlotOrientation orientation = plot.getOrientation();
        final AxisLocation domainAxisLocation = plot.getDomainAxisLocation();
        final AxisLocation rangeAxisLocation = plot.getRangeAxisLocation();
        final RectangleEdge domainEdge = Plot.resolveDomainAxisLocation(domainAxisLocation, orientation);
        final RectangleEdge rangeEdge = Plot.resolveRangeAxisLocation(rangeAxisLocation, orientation);
        final Range xRange = domainAxis.getRange();
        final Range yRange = rangeAxis.getRange();
        final double anchorX = xRange.getLowerBound() + (0.5D * xRange.getLength());
        final double anchorY = yRange.getLowerBound();

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

        final double maxW = dataArea.getWidth();
        final double maxH = dataArea.getHeight();
        final RectangleConstraint rc = new RectangleConstraint(new Range(0, maxW), new Range(0, maxH));

        final Size2D size = this.title.arrange(g2, rc);
        final Rectangle2D titleRect = new Rectangle2D.Double(0, 0, size.width, size.height);
        final Point2D anchorPoint = RectangleAnchor.BOTTOM.getAnchorPoint(titleRect);
        xx = xx - (float) anchorPoint.getX();
        yy = yy - (float) anchorPoint.getY();

        titleRect.setRect(xx, yy, titleRect.getWidth(), titleRect.getHeight());
        final BlockParams p = new BlockParams();
        if (info != null) {
            if (info.getOwner().getEntityCollection() != null) {
                p.setGenerateEntities(true);
            }
        }
        final Object result = this.title.draw(g2, titleRect, p);
        if (info != null) {
            if (result instanceof EntityBlockResult) {
                final EntityBlockResult ebr = (EntityBlockResult) result;
                info.getOwner().getEntityCollection().addAll(ebr.getEntityCollection());
            }
            final String toolTip = getToolTipText();
            final String url = getURL();
            final Rectangle2D.Float notePosition = new Rectangle2D.Float(xx, yy, (float) size.width,
                    (float) size.height);
            addEntity(info, notePosition, rendererIndex, toolTip, url);

            final Rectangle2D noteIconPosition = (Rectangle2D) noteIconAnnotation.getEntity().getArea();
            final XYLineAnnotation lineAnnotation = new XYLineAnnotation(notePosition.getCenterX(), notePosition.getY(),
                    noteIconPosition.getCenterX(), noteIconPosition.getMaxY(), NOTE_STROKE, NOTE_BORDER_COLOR);
            lineAnnotation.draw(g2, plot, dataArea, XYPlots.DRAWING_ABSOLUTE_AXIS, XYPlots.DRAWING_ABSOLUTE_AXIS,
                    rendererIndex, null);
        }

    }

    public XYAnnotationEntity getEntity() {
        return entity;
    }

    public void setEntity(final XYAnnotationEntity entity) {
        this.entity = entity;
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
        final XYAnnotationEntity entity = newEntity(hotspot, rendererIndex, toolTipText, urlText);
        entities.add(entity);
        this.entity = entity;
    }

    protected XYAnnotationEntity newEntity(final Shape hotspot, final int rendererIndex, final String toolTipText,
            final String urlText) {
        return new XYNoteAnnotationEntity(this, hotspot, rendererIndex, toolTipText, urlText);
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }

    @Override
    public boolean equals(final Object obj) {
        return obj == this;
    }

}
