package de.invesdwin.context.jasperreports;

import javax.annotation.concurrent.NotThreadSafe;

import net.sf.dynamicreports.report.constant.HorizontalAlignment;

@NotThreadSafe
public class DynamicColumn {
    private String name;
    private String title;
    private String type;
    private String pattern;
    private HorizontalAlignment horizontalAlignment;

    public DynamicColumn(final String title, final String name, final String type) {
        this.name = name;
        this.type = type;
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(final String pattern) {
        this.pattern = pattern;
    }

    public HorizontalAlignment getHorizontalAlignment() {
        return horizontalAlignment;
    }

    public void setHorizontalAlignment(final HorizontalAlignment horizontalAlignment) {
        this.horizontalAlignment = horizontalAlignment;
    }

}
