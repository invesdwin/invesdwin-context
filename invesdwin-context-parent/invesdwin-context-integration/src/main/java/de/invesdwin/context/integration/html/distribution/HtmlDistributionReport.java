package de.invesdwin.context.integration.html.distribution;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.context.integration.csv.writer.HtmlTableTheme;
import de.invesdwin.context.integration.csv.writer.HtmlTableWriter;
import de.invesdwin.context.integration.html.helper.AHtmlPanelGenerator;
import de.invesdwin.context.integration.html.helper.AHtmlPlotlyGenerator;
import de.invesdwin.context.integration.html.helper.AHtmlPlotlyPlotGenerator;
import de.invesdwin.util.math.decimal.Decimal;
import de.invesdwin.util.math.decimal.scaled.Percent;
import de.invesdwin.util.math.decimal.scaled.PercentScale;

@NotThreadSafe
public class HtmlDistributionReport {

    public static final int DEFAULT_BIN_COUNT = 50;
    public static final String DEFAULT_HTML_FILE_NAME = "Distribution.html";
    public static final String DECIMAL_FORMAT = "0.00000";

    protected String newHtmlFileName() {
        return DEFAULT_HTML_FILE_NAME;
    }

    public void writeReport(final File file, final List<? extends DistributionMeasure> measures) {
        for (int m = 0; m < measures.size(); m++) {
            measures.get(m).sort();
        }

        new AHtmlPlotlyGenerator() {
            @Override
            protected String getTitle() {
                final StringBuilder sb = new StringBuilder();
                sb.append(file.getParentFile().getName());
                sb.append(" - Distribution");
                sb.append(" - invesdwin");
                return sb.toString();
            }

            @Override
            protected void appendBody(final PrintWriter writer) {
                for (int i = 0; i < measures.size(); i++) {
                    writer.append("\n<div class=\"row\">");
                    final DistributionMeasure measure = measures.get(i);
                    appendPlot(writer, new DistributionPlotlyPlotGenerator(measure, i));
                    appendConfidenceLevels(writer, measure, i);
                    writer.append("\n</div>");
                }
            }

            private void appendPlot(final PrintWriter writer, final AHtmlPlotlyPlotGenerator plot) {
                writer.append("\n<div class=\"col-sm-8\" style=\"padding: 5px\">");
                plot.appendPlot(writer);
                writer.append("\n</div>");
            }

            @SuppressWarnings("resource")
            private void appendConfidenceLevels(final PrintWriter writer, final DistributionMeasure measure,
                    final int index) {
                writer.append("\n<div class=\"col-sm-4\" style=\"padding: 5px\">");

                new AHtmlPanelGenerator() {

                    @Override
                    protected String getTitle() {
                        final StringBuilder writer = new StringBuilder();
                        writer.append(measure.getMeasureName());
                        writer.append(" Confidence Levels");
                        return writer.toString();
                    }

                    @Override
                    protected String getSuffix() {
                        return "ConfidenceLevels" + index;
                    }

                    @Override
                    protected void appendPanelBody(final PrintWriter writer) {
                        try (HtmlTableWriter table = new HtmlTableWriter(writer).setTheme(HtmlTableTheme.BOOTSTRAP)
                                .setCloseOut(false)) {
                            table.line("Confidence Level", measure.getMeasureName());
                            for (final Percent confidenceLevel : newConfidenceLevels()) {
                                table.line(confidenceLevel.toString(PercentScale.PERCENT),
                                        measure.getConfidenceLevelValue(confidenceLevel)
                                                .toFormattedString(getDecimalFormat()));
                            }
                            table.line("<i>Min</i>", measure.getMin().toFormattedString(getDecimalFormat()));
                            table.line("<i>Max</i>", measure.getMax().toFormattedString(getDecimalFormat()));
                            table.line("<i>Avg</i>", measure.getAvg().toFormattedString(getDecimalFormat()));
                            table.line("<i>Median</i>", measure.getMedian().toFormattedString(getDecimalFormat()));
                            table.line("<i>Stdev</i>", measure.getStdev().toFormattedString(getDecimalFormat()));
                            table.line("<i>Range</i>", measure.getRange().toFormattedString(getDecimalFormat()));
                            table.line("<i>IQ-Range</i>", measure.getIQRange().toFormattedString(getDecimalFormat()));
                        } catch (final IOException e) {
                            throw new RuntimeException(e);
                        }
                    }

                }.appendPanel(writer);

                writer.append("\n</div>");
            }

        }.writeToFile(file);
    }

    protected int getBinCount() {
        return DEFAULT_BIN_COUNT;
    }

    protected String getDecimalFormat() {
        return DECIMAL_FORMAT;
    }

    protected List<Percent> newConfidenceLevels() {
        final List<Percent> confidenceLevels = new ArrayList<Percent>();
        confidenceLevels.add(new Percent(1D, PercentScale.PERCENT));
        confidenceLevels.add(new Percent(2D, PercentScale.PERCENT));
        confidenceLevels.add(new Percent(5D, PercentScale.PERCENT));
        confidenceLevels.add(new Percent(25D, PercentScale.PERCENT));
        confidenceLevels.add(new Percent(50D, PercentScale.PERCENT));
        confidenceLevels.add(new Percent(75D, PercentScale.PERCENT));
        confidenceLevels.add(new Percent(95D, PercentScale.PERCENT));
        confidenceLevels.add(new Percent(98D, PercentScale.PERCENT));
        confidenceLevels.add(new Percent(99D, PercentScale.PERCENT));
        return confidenceLevels;
    }

    private final class DistributionPlotlyPlotGenerator extends AHtmlPlotlyPlotGenerator {

        private final DistributionMeasure measure;
        private final int index;

        private DistributionPlotlyPlotGenerator(final DistributionMeasure measure, final int index) {
            this.measure = measure;
            this.index = index;
        }

        @Override
        protected String getSuffix() {
            return "Distribution" + index;
        }

        @Override
        protected String getTitle() {
            final StringBuilder writer = new StringBuilder();
            writer.append(measure.getMeasureName());
            writer.append(" Distribution of ");
            writer.append(String.valueOf(measure.getValues().size()));
            writer.append(" Values");
            return writer.toString();
        }

        @Override
        protected void appendLayout(final PrintWriter writer) {
            //  var layout = {barmode: 'overlay'};
            writer.append("\nvar layout" + getSuffix() + " = {");
            writer.append("\n  barmode: 'overlay',");
            writer.append("\n  bargap: 0.05,");
            writer.append("\n  bargroupgap: 0.2,");
            writer.append("\n  showlegend: false,");
            writer.append("\n  legend: {orientation: 'h'},");
            writer.append("\n  autosize: true,");
            writer.append("\n  xaxis: {");
            writer.append("\n    title: '" + measure.getMeasureName() + "'");
            writer.append("\n  },");
            writer.append("\n  yaxis: {");
            writer.append("\n    title: 'Count'");
            writer.append("\n  }");
            writer.append("\n};");
        }

        @Override
        protected void appendData(final PrintWriter writer) {
            appendTrace(writer);

            //  var data = [trace1, trace2];
            writer.append("\nvar data" + getSuffix() + " = [trace" + getSuffix() + "];");
        }

        private void appendTrace(final PrintWriter writer) {
            //  var trace1 = {
            writer.append("\nvar trace" + getSuffix() + " = {");
            //    x: ['giraffes', 'orangutans', 'monkeys'],
            writer.append("\n  x: [");
            for (int i = 0; i < measure.getValues().size(); i++) {
                if (i > 0) {
                    writer.append(", ");
                }
                writer.append(String.valueOf(measure.getValues().get(i).doubleValue()));
            }
            writer.append("],");
            //    name: 'SF Zoo',
            writer.append("\n  name: '" + measure.getMeasureName() + "',");
            //    type: 'bar'
            writer.append("\n  type: 'histogram',");

            //            xbins: {
            writer.append("\n  xbins: {");
            //                end: 4,
            final Decimal start = measure.getStart();
            final Decimal end = measure.getEnd();
            final Decimal size = end.subtract(start).divide(getBinCount());
            writer.append("\n    end: " + end.doubleValue() + ",");
            //                size: 0.06,
            writer.append("\n    size: " + size.doubleValue() + ",");
            //                start: -3.2
            writer.append("\n    start: " + start.doubleValue() + ",");
            //              }
            writer.append("\n  },");

            //  };
            writer.append("\n};");
        }

    }

}
