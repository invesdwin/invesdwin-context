package de.invesdwin.context.integration.html.helper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.lang.Files;

@NotThreadSafe
public abstract class AHtmlReportGenerator {

    public void writeToFile(final File file) {
        try {
            Files.forceMkdirParent(file);
            final PrintWriter writer = new PrintWriter(new FileOutputStream(file));
            writer.append("<!DOCTYPE HTML>");
            writer.append("\n<html lang=\"en\" class=\"no-js theme-light\">");
            writer.append("\n<head>");
            writer.append("\n\t<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">");
            writer.append("\n\t<meta charset=\"UTF-8\">");
            writer.append(
                    "\n\t<link href=\"https://cdn.jsdelivr.net/npm/bootstrap@5.2.1/dist/css/bootstrap.min.css\" rel=\"stylesheet\" integrity=\"sha384-iYQeCzEYFbKjA/T2uDLTpkwGzCiq6soy8tYaI1GyVh/UjpbCx/TYkiZhlZB6+fzT\" crossorigin=\"anonymous\">");
            writer.append(
                    "\n\t<script src=\"https://cdn.jsdelivr.net/npm/bootstrap@5.2.1/dist/js/bootstrap.bundle.min.js\" integrity=\"sha384-u1OknCvxWvY5kfmNBILK2hRnQC3Pr17a+RTT6rIHI7NnikvbZlHgTPOOmMi466C8\" crossorigin=\"anonymous\"></script>");
            //            <title>INV_ReverseRsiSystemStrategy - ABacktestBroker[2_0] - Log - Trading Strategy - invesdwin-trading</title>
            writer.append("\n\t<title>");
            final String title = getTitle();
            writer.append(title);
            writer.append("</title>");
            appendHead(writer);
            writer.append("\n</head>");
            writer.append("\n<body>");

            appendNav(writer, title);

            writer.append("\n\t<div class=\"");
            writer.append(getContainerClass());
            writer.append("\">");

            appendBody(writer);

            writer.append("\n\t</div>");
            writer.append("\n</body>\n</html>");
            writer.close();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected String getContainerClass() {
        return "container-fluid";
    }

    protected void appendNav(final PrintWriter writer, final String title) {
        writer.append(
                "\n\t<nav class=\"navbar navbar-light navbar-expand-lg bg-light\" style=\"margin-bottom: 20px; border-bottom: 1px solid rgba(127, 127, 127, 0.25) !important;\">");
        writer.append("\n\t\t<div class=\"container-fluid\">");
        writer.append("\n\t\t\t<div class=\"navbar-header\">");
        writer.append("\n\t\t\t\t<span class=\"navbar-brand\">");
        writer.append(title);
        writer.append("</span>");
        writer.append("\n\t\t\t</div>");
        writer.append("\n\t\t</div>");
        writer.append("\n\t</nav>");
    }

    protected abstract void appendHead(PrintWriter writer);

    protected abstract void appendBody(PrintWriter writer);

    protected abstract String getTitle();

}
