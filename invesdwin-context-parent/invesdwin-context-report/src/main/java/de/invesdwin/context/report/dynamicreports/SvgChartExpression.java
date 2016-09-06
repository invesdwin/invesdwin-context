package de.invesdwin.context.report.dynamicreports;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.Callable;

import javax.annotation.concurrent.NotThreadSafe;

import org.jfree.chart.JFreeChart;

import de.invesdwin.context.log.error.Err;
import de.invesdwin.context.report.jfreechart.JFreeChartExporter;
import de.invesdwin.context.report.jfreechart.JFreeChartExporterSettings;
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
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            JFreeChartExporter.SVG.writeChart(out, jfreeChart, settings);
            return new BatikRenderer(out.toByteArray(), null);
        } catch (final Exception e) {
            throw Err.process(e);
        }
    }

}
