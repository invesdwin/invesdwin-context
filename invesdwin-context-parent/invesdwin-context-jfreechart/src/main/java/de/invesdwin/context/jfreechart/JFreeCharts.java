package de.invesdwin.context.jfreechart;

import javax.annotation.concurrent.Immutable;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.general.Series;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.data.xy.YIntervalSeries;
import org.jfree.data.xy.YIntervalSeriesCollection;

@Immutable
public final class JFreeCharts {

    private JFreeCharts() {
    }

    public static TextTitle newSubtitle(final JFreeChart chart, final String subtitle) {
        return new TextTitle(subtitle,
                chart.getTitle().getFont().deriveFont(0, chart.getTitle().getFont().getSize2D() * 0.7f));
    }

    public static YIntervalSeries newYIntervalSeries(final String title) {
        final YIntervalSeries series = new YIntervalSeries(title);
        series.setNotify(false);
        return series;
    }

    public static void fireSeriesChangedOn(final XYSeriesCollection dataset) {
        for (int i = 0; i < dataset.getSeriesCount(); i++) {
            fireSeriesChanged(dataset.getSeries(i));
        }
    }

    public static void fireSeriesChangedOn(final YIntervalSeriesCollection dataset) {
        for (int i = 0; i < dataset.getSeriesCount(); i++) {
            fireSeriesChanged(dataset.getSeries(i));
        }
    }

    public static void fireSeriesChanged(final Series... series) {
        for (final Series s : series) {
            fireSeriesChanged(s);
        }
    }

    public static void fireSeriesChanged(final Series s) {
        s.setNotify(true);
        s.fireSeriesChanged();
    }

    public static XYSeries newXYSeries(final String title) {
        final XYSeries series = new XYSeries(title);
        series.setNotify(false);
        return series;
    }

}
