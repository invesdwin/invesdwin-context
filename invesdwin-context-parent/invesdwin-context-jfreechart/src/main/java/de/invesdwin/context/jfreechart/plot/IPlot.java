package de.invesdwin.context.jfreechart.plot;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItemSource;
import org.jfree.chart.event.AnnotationChangeListener;
import org.jfree.chart.event.AxisChangeListener;
import org.jfree.chart.event.MarkerChangeListener;
import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.chart.event.PlotChangeListener;
import org.jfree.chart.plot.DrawingSupplier;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.PlotState;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.data.general.DatasetChangeListener;
import org.jfree.data.general.DatasetGroup;

public interface IPlot extends AxisChangeListener, DatasetChangeListener, AnnotationChangeListener,
        MarkerChangeListener, LegendItemSource {

    Plot getPlot();

    JFreeChart getChart();

    void setChart(JFreeChart chart);

    boolean fetchElementHintingFlag();

    DatasetGroup getDatasetGroup();

    String getNoDataMessage();

    void setNoDataMessage(String message);

    Font getNoDataMessageFont();

    void setNoDataMessageFont(Font font);

    Paint getNoDataMessagePaint();

    void setNoDataMessagePaint(Paint paint);

    String getPlotType();

    Plot getParent();

    void setParent(Plot parent);

    Plot getRootPlot();

    boolean isSubplot();

    RectangleInsets getInsets();

    void setInsets(RectangleInsets insets);

    void setInsets(RectangleInsets insets, boolean notify);

    Paint getBackgroundPaint();

    void setBackgroundPaint(Paint paint);

    float getBackgroundAlpha();

    void setBackgroundAlpha(float alpha);

    DrawingSupplier getDrawingSupplier();

    void setDrawingSupplier(DrawingSupplier supplier);

    void setDrawingSupplier(DrawingSupplier supplier, boolean notify);

    Image getBackgroundImage();

    void setBackgroundImage(Image image);

    int getBackgroundImageAlignment();

    void setBackgroundImageAlignment(int alignment);

    float getBackgroundImageAlpha();

    void setBackgroundImageAlpha(float alpha);

    boolean isOutlineVisible();

    void setOutlineVisible(boolean visible);

    Stroke getOutlineStroke();

    void setOutlineStroke(Stroke stroke);

    Paint getOutlinePaint();

    void setOutlinePaint(Paint paint);

    float getForegroundAlpha();

    void setForegroundAlpha(float alpha);

    boolean isNotify();

    void setNotify(boolean notify);

    void addChangeListener(PlotChangeListener listener);

    void removeChangeListener(PlotChangeListener listener);

    void notifyListeners(PlotChangeEvent event);

    void draw(Graphics2D g2, Rectangle2D area, Point2D anchor, PlotState parentState, PlotRenderingInfo info);

    void drawBackground(Graphics2D g2, Rectangle2D area);

    void drawBackgroundImage(Graphics2D g2, Rectangle2D area);

    void drawOutline(Graphics2D g2, Rectangle2D area);

    void handleClick(int x, int y, PlotRenderingInfo info);

    void zoom(double percent);

}
