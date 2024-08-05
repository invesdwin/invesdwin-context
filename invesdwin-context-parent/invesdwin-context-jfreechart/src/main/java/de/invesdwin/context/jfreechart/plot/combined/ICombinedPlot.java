package de.invesdwin.context.jfreechart.plot.combined;

import java.awt.geom.Point2D;
import java.util.List;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.data.Range;

import de.invesdwin.context.jfreechart.axis.AxisType;
import de.invesdwin.context.jfreechart.plot.IAxisPlot;

public interface ICombinedPlot extends IAxisPlot {

    AxisType getCombinedAxisType();

    double getGap();

    void setGap(double gap);

    void add(Plot subplot);

    void add(Plot subplot, int weight);

    void remove(Plot subplot);

    List<? extends Plot> getSubplots();

    Plot findSubplot(PlotRenderingInfo info, Point2D source);

    Range getDataRange(ValueAxis axis);

    void plotChanged(PlotChangeEvent event);
}
