package de.invesdwin.context.jfreechart.axis;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.text.AttributedString;
import java.util.EventListener;
import java.util.List;

import org.jfree.chart.axis.Axis;
import org.jfree.chart.axis.AxisLabelLocation;
import org.jfree.chart.axis.AxisSpace;
import org.jfree.chart.axis.AxisState;
import org.jfree.chart.axis.Tick;
import org.jfree.chart.event.AxisChangeListener;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.chart.ui.RectangleInsets;

import de.invesdwin.context.jfreechart.plot.IAxisPlot;

public interface IAxis {

    IAxisPlot getPlot();

    Axis getAxis();

    boolean isVisible();

    void setVisible(boolean flag);

    String getLabel();

    void setLabel(String label);

    AttributedString getAttributedLabel();

    void setAttributedLabel(String label);

    void setAttributedLabel(AttributedString label);

    AttributedString createAttributedLabel(String label);

    Font getLabelFont();

    void setLabelFont(Font font);

    Paint getLabelPaint();

    void setLabelPaint(Paint paint);

    RectangleInsets getLabelInsets();

    void setLabelInsets(RectangleInsets insets);

    void setLabelInsets(RectangleInsets insets, boolean notify);

    double getLabelAngle();

    void setLabelAngle(double angle);

    AxisLabelLocation getLabelLocation();

    void setLabelLocation(AxisLabelLocation location);

    boolean isAxisLineVisible();

    void setAxisLineVisible(boolean visible);

    Paint getAxisLinePaint();

    void setAxisLinePaint(Paint paint);

    Stroke getAxisLineStroke();

    void setAxisLineStroke(Stroke stroke);

    boolean isTickLabelsVisible();

    void setTickLabelsVisible(boolean flag);

    boolean isMinorTickMarksVisible();

    void setMinorTickMarksVisible(boolean flag);

    Font getTickLabelFont();

    void setTickLabelFont(Font font);

    Paint getTickLabelPaint();

    void setTickLabelPaint(Paint paint);

    RectangleInsets getTickLabelInsets();

    void setTickLabelInsets(RectangleInsets insets);

    boolean isTickMarksVisible();

    void setTickMarksVisible(boolean flag);

    float getTickMarkInsideLength();

    void setTickMarkInsideLength(float length);

    float getTickMarkOutsideLength();

    void setTickMarkOutsideLength(float length);

    Stroke getTickMarkStroke();

    void setTickMarkStroke(Stroke stroke);

    Paint getTickMarkPaint();

    void setTickMarkPaint(Paint paint);

    float getMinorTickMarkInsideLength();

    void setMinorTickMarkInsideLength(float length);

    float getMinorTickMarkOutsideLength();

    void setMinorTickMarkOutsideLength(float length);

    double getFixedDimension();

    void setFixedDimension(double dimension);

    void configure();

    AxisSpace reserveSpace(Graphics2D g2, Plot plot, Rectangle2D plotArea, RectangleEdge edge, AxisSpace space);

    AxisState draw(Graphics2D g2, double cursor, Rectangle2D plotArea, Rectangle2D dataArea, RectangleEdge edge,
            PlotRenderingInfo plotState);

    List<Tick> refreshTicks(Graphics2D g2, AxisState state, Rectangle2D dataArea, RectangleEdge edge);

    void addChangeListener(AxisChangeListener listener);

    void removeChangeListener(AxisChangeListener listener);

    boolean hasListener(EventListener listener);

}
