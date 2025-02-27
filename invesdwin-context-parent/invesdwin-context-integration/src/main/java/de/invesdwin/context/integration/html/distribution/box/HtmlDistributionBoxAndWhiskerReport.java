package de.invesdwin.context.integration.html.distribution.box;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.context.integration.html.distribution.DistributionMeasure;
import de.invesdwin.context.integration.html.helper.AHtmlPlotlyGenerator;
import de.invesdwin.context.integration.html.helper.AHtmlPlotlyPlotGenerator;
import de.invesdwin.util.lang.string.Strings;
import de.invesdwin.util.math.statistics.distribution.ADistributionComparator;
import de.invesdwin.util.math.statistics.distribution.AZScoreComparator;

@Immutable
public class HtmlDistributionBoxAndWhiskerReport {

    public static final AZScoreComparator<DistributionMeasure> DEFAULT_DISTRIBUTION_COMPARATOR = DistributionMeasure.COMPARATOR_ZSCORE;

    protected ADistributionComparator<DistributionMeasure> newDistributionComparator() {
        return DEFAULT_DISTRIBUTION_COMPARATOR;
    }

    public void writeReport(final File file, final Map<String, List<DistributionMeasure>> measure_samples)
            throws IOException {
        new AHtmlPlotlyGenerator() {

            @Override
            protected String getTitle() {
                return "Distribution Box And Whisker - " + newDistributionComparator().getStatisticName();
            }

            @Override
            protected void appendBody(final PrintWriter writer) {
                for (final Entry<String, List<DistributionMeasure>> entry : measure_samples.entrySet()) {
                    final String measureName = entry.getKey();
                    final List<DistributionMeasure> samples = entry.getValue();
                    if (samples.size() <= 1) {
                        //not enough samples to compare
                        continue;
                    }
                    final DistributionRankingSampleMetric metric = new DistributionRankingSampleMetric(samples,
                            newDistributionComparator());
                    final String suffix = Strings.stripNonAlphanumeric(measureName);
                    new BoxAndWhiskerPlot(metric, measureName, suffix, samples).appendPlot(writer);
                }
            }
        }.writeToFile(file);
    }

    public static final class BoxAndWhiskerPlot extends AHtmlPlotlyPlotGenerator {
        private final DistributionRankingSampleMetric metric;
        private final String measureName;
        private final String suffix;
        private final List<DistributionMeasure> samples;

        public BoxAndWhiskerPlot(final DistributionRankingSampleMetric metric, final String measureName,
                final String suffix, final List<DistributionMeasure> samples) {
            this.metric = metric;
            this.measureName = measureName;
            this.suffix = suffix;
            this.samples = samples;
        }

        @Override
        protected String getTitle() {
            return measureName;
        }

        @Override
        protected String getSuffix() {
            return suffix;
        }

        @Override
        protected void appendData(final PrintWriter writer) {
            writer.append("var xData" + getSuffix() + " = [");
            for (int i = 0; i < samples.size(); i++) {
                //            var xData = ['Carmelo<br>Anthony', 'Dwyane<br>Wade',
                //                         'Deron<br>Williams', 'Brook<br>Lopez',
                //                         'Damian<br>Lillard', 'David<br>West',
                //                         'Blake<br>Griffin', 'David<br>Lee',
                //                         'Demar<br>Derozan'];
                final DistributionMeasure cur = metric.getSamples().get(i);
                if (i > 0) {
                    writer.append(", ");
                }
                writer.append("\n");
                writer.append("'");
                writer.append(String.valueOf(i + 1));
                writer.append(": ");
                writer.append(cur.getSampleName());
                writer.append("'");
            }
            writer.append("];\n");

            writer.append("var yData" + getSuffix() + " = [");
            for (int i = 0; i < samples.size(); i++) {
                //                   var yData = [
                //                           getrandom(30 ,10),
                //                           getrandom(30, 20),
                //                           getrandom(30, 25),
                //                           getrandom(30, 40),
                //                           getrandom(30, 45),
                //                           getrandom(30, 30),
                //                           getrandom(30, 20),
                //                           getrandom(30, 15),
                //                           getrandom(30, 43),
                //                       ];
                final DistributionMeasure cur = metric.getSamples().get(i);
                if (i > 0) {
                    writer.append(", ");
                }
                writer.append("\n");
                writer.append("[");
                for (int v = 0; v < cur.getCount(); v++) {
                    if (v > 0) {
                        writer.append(", ");
                        if (v % 100 == 0) {
                            writer.append("\n");
                        }
                    }
                    writer.append(String.valueOf(cur.getValues().get(v)));
                }
                writer.append("]");
            }
            writer.append("\n];\n");
            //                   var data = [];
            writer.append("var data" + getSuffix() + " = [];\n");
            //                   for ( var i = 0; i < xData.length; i ++ ) {
            writer.append("for ( var i = 0; i < xData" + getSuffix() + ".length; i++ ) {\n");
            //                       var result = {
            writer.append("\tvar result = {\n");
            //                           type: 'box',
            writer.append("\t\ttype: 'box',\n");
            //                           y: yData[i],
            writer.append("\t\ty: yData" + getSuffix() + "[i],\n");
            //                           name: xData[i],
            writer.append("\t\tname: xData" + getSuffix() + "[i],\n");
            writer.append("\t\tboxmean: 'sd',\n");
            //                           boxpoints: 'all',
            //            if (samples.size() <= MAX_BOX_POINTS_SAMPLES) {
            //                //                boxpoints='all',
            //                writer.append("\t\tboxpoints: 'suspectedoutliers',\n");
            //            } else {
            writer.append("\t\tboxpoints: false,\n");
            //            }
            //                       };
            writer.append("\t};\n");
            //                       data.push(result);
            writer.append("\tdata" + getSuffix() + ".push(result);\n");
            //                   };
            writer.append("};\n");
        }

        @Override
        protected void appendLayout(final PrintWriter writer) {
            //                   layout = {
            writer.append("layout" + getSuffix() + " = {\n");
            //                       title: 'Points Scored by the Top 9 Scoring NBA Players in 2012',
            //                       margin: {
            writer.append("\tmargin: {\n");
            //                           l: 40,
            writer.append("\t\tl: 50,\n");
            //                           r: 30,
            writer.append("\t\tr: 10,\n");
            //                           b: 80,
            writer.append("\t\tb: 100,\n");
            //                           t: 100
            writer.append("\t\tt: 10,\n");
            writer.append("\t\tpad: 4\n");
            //                       },
            writer.append("\t},\n");
            //                       showlegend: false
            writer.append("\tshowlegend: false\n");
            //                   };
            writer.append("};\n");
        }
    }

}
