package de.invesdwin.context.integration.html.helper;

import java.io.PrintWriter;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public abstract class AHtmlPlotlyPlotGenerator {

    public void appendPlot(final PrintWriter writer) {
        final String suffix = getSuffix();

        new AHtmlPanelGenerator() {

            @Override
            protected String getTitle() {
                return AHtmlPlotlyPlotGenerator.this.getTitle();
            }

            @Override
            protected String getSuffix() {
                return AHtmlPlotlyPlotGenerator.this.getSuffix();
            }

            @Override
            protected void appendPanelBody(final PrintWriter writer) {
                writer.append("\n        <div id=\"plot" + suffix + "\"></div>");
            }
        }.appendPanel(writer);

        appendScript(writer, suffix);
    }

    protected abstract String getTitle();

    protected void appendScript(final PrintWriter writer, final String suffix) {
        writer.append("\n<script>");

        appendData(writer);
        appendLayout(writer);

        //  Plotly.newPlot('priceProfilePlot', data, layout);
        //https://codepen.io/nicolaskruchten/pen/ERgBZX
        writer.append("\nconst Plot" + suffix + " = createPlotlyComponent(Plotly);");

        writer.append("\nReactDOM.render(");
        writer.append("\n  React.createElement(Plot" + suffix + ", {");
        writer.append("\n    data: data" + suffix + ",");
        writer.append("\n    layout: layout" + suffix + ",");
        appendPlotOptions(writer);
        writer.append("\n    useResizeHandler: true,");
        writer.append("\n    style: {width: \"100%\", height: \"100%\"}");
        writer.append("\n  }),");
        writer.append("\n  document.getElementById('plot" + suffix + "')");
        writer.append("\n);");

        writer.append("\n</script>");
    }

    protected void appendPlotOptions(final PrintWriter writer) {}

    protected String getSuffix() {
        return "";
    }

    protected abstract void appendLayout(PrintWriter writer);

    protected abstract void appendData(PrintWriter writer);

}
