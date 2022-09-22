package de.invesdwin.context.integration.csv.writer;

import javax.annotation.concurrent.Immutable;

@Immutable
public class HtmlTableTheme {

    public static final HtmlTableTheme NO_STYLE = new HtmlTableTheme() {
        @Override
        public String styleOpenCloseTag() {
            return "";
        }
    };
    public static final HtmlTableTheme DEFAULT = new HtmlTableTheme();

    public static final HtmlTableTheme BOOTSTRAP = new HtmlTableTheme() {
        @Override
        public String styleOpenCloseTag() {
            return "";
        }

        @Override
        public String tableOpenTag() {
            return "<div class=\"table-responsive\"><table class=\"table table-sm table-hover table-bordered table-striped\">";
        }

        @Override
        public String tableCloseTag() {
            return "</table></div>";
        }

        @Override
        public String thOpenTag() {
            return "<th class=\"text-center\">";
        }

        @Override
        public String tdOpenTag() {
            return "<td class=\"text-right\">";
        }
    };

    public char lineFeed() {
        return '\n';
    }

    public String styleOpenCloseTag() {
        final StringBuilder sb = new StringBuilder();
        sb.append("<style>");
        sb.append(lineFeed());
        sb.append("td, th { border: 1px solid; padding-left: 5px; padding-right: 5px; }");
        sb.append(lineFeed());
        sb.append("table { border-collapse: collapse; }");
        sb.append(lineFeed());
        sb.append("thead { text-align: center; }");
        sb.append(lineFeed());
        sb.append("tbody { text-align: right; }");
        sb.append(lineFeed());
        sb.append("</style>");
        return sb.toString();
    }

    public String tableOpenTag() {
        return "<table>";
    }

    public String tableCloseTag() {
        return "</table>";
    }

    public String theadOpenTag() {
        return "<thead>";
    }

    public String theadCloseTag() {
        return "</thead>";
    }

    public String tbodyOpenTag() {
        return "<tbody>";
    }

    public String tbodyCloseTag() {
        return "</tbody>";
    }

    public String trOpenTag() {
        return "<tr>";
    }

    public String trCloseTag() {
        return "</tr>";
    }

    public String thOpenTag() {
        return "<th>";
    }

    public String thCloseTag() {
        return "</th>";
    }

    public String tdOpenTag() {
        return "<td>";
    }

    public String tdCloseTag() {
        return "</td>";
    }

}
