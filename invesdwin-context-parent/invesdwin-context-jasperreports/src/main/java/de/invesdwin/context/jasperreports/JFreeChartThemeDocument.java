package de.invesdwin.context.jasperreports;

import java.awt.Paint;

import javax.annotation.concurrent.Immutable;

import org.jfree.chart.JFreeChart;

import de.invesdwin.context.jfreechart.visitor.JFreeChartThemeSwing;

@Immutable
public class JFreeChartThemeDocument extends JFreeChartThemeSwing {

    public static final boolean DEFAULT_OUTLINE_VISIBLE = false;
    public static final Paint DEFAULT_GRID_PAINT = JFreeChartThemeSwing.DEFAULT_OUTLINE_PAINT;

    @Override
    protected void processChart(final JFreeChart chart) {
        super.processChart(chart);
        chart.setBackgroundPaint(DEFAULT_BACKGROUND_PAINT);
    }

    @Override
    protected Paint getGridlinePaint() {
        return DEFAULT_GRID_PAINT;
    }

    @Override
    protected boolean isOutlineVisible() {
        return DEFAULT_OUTLINE_VISIBLE;
    }

}
