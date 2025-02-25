package de.invesdwin.context.integration.html.helper;

import java.io.PrintWriter;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public abstract class AHtmlPanelGenerator {

    public void appendPanel(final PrintWriter writer) {
        final String suffix = getSuffix();

        //        <div class="card">
        writer.append("\n<div class=\"card\">");
        //          <div class="card-header">
        writer.append("\n  <div class=\"card-header\">");
        //              <a data-bs-toggle="collapse" href="#collapse1">Collapsible panel</a>
        writer.append("\n    <a data-bs-toggle=\"collapse\" href=\"#collapse" + suffix + "\">" + getTitle() + "</a>");
        //          </div>
        writer.append("\n  </div>");
        //          <div id="collapse1" class="collapse">
        writer.append("\n  <div id=\"collapse" + suffix + "\" class=\"collapse show\">");
        //            <div class="card-body">Panel Body</div>
        writer.append("\n    <div class=\"card-body\" " + getPanelBodyStyle() + ">");
        appendPanelBody(writer);
        writer.append("\n    </div>");
        //          </div>
        writer.append("\n  </div>");
        //        </div>
        writer.append("\n</div>");
    }

    protected String getPanelBodyStyle() {
        return "style=\"padding: 0\"";
    }

    protected abstract void appendPanelBody(PrintWriter writer);

    protected abstract String getTitle();

    protected String getSuffix() {
        return "";
    }

}
