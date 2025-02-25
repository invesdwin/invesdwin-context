package de.invesdwin.context.integration.html.helper;

import java.io.PrintWriter;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public abstract class AHtmlPlotlyGenerator extends AHtmlReportGenerator {

    @Override
    protected void appendHead(final PrintWriter writer) {
        writer.append("\n\t<script src=\"https://unpkg.com/react@16/umd/react.development.js\"></script>");
        writer.append("\n\t<script src=\"https://unpkg.com/react-dom@16/umd/react-dom.development.js\"></script>");
        writer.append("\n\t<script src=\"https://cdn.plot.ly/plotly-latest.min.js\"></script>");
        writer.append(
                "\n\t<script src=\"https://unpkg.com/react-plotly.js@2.2.0/dist/create-plotly-component.js\"></script>");
    }

    @Override
    protected abstract void appendBody(PrintWriter writer);

}