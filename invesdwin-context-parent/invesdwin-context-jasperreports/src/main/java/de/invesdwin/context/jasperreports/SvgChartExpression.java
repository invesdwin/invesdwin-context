package de.invesdwin.context.jasperreports;

import java.util.concurrent.Callable;

import javax.annotation.concurrent.NotThreadSafe;

import org.jfree.chart.JFreeChart;

import de.invesdwin.context.log.error.Err;
import de.invesdwin.util.streams.pool.PooledFastByteArrayOutputStream;
import net.sf.dynamicreports.report.base.expression.AbstractSimpleExpression;
import net.sf.dynamicreports.report.definition.ReportParameters;
import net.sf.jasperreports.engine.Renderable;
import net.sf.jasperreports.renderers.BatikRenderer;

@SuppressWarnings("serial")
@NotThreadSafe
public class SvgChartExpression extends AbstractSimpleExpression<Renderable> {

    private final Callable<JFreeChart> chart;
    private final JFreeChartExporterSettings settings;

    public SvgChartExpression(final Callable<JFreeChart> chart, final JFreeChartExporterSettings settings) {
        this.chart = chart;
        this.settings = settings;
    }

    @Override
    public Renderable evaluate(final ReportParameters reportParameters) {
        try {
            final JFreeChart jfreeChart = chart.call();
            try (PooledFastByteArrayOutputStream out = PooledFastByteArrayOutputStream.newInstance()) {
                JFreeChartExporter.SVG.writeChart(out.asNonClosing(), jfreeChart, settings);
                return new BatikRenderer(out.toByteArray(), null);
            }
        } catch (final Exception e) {
            throw Err.process(e);
        }
    }

}
