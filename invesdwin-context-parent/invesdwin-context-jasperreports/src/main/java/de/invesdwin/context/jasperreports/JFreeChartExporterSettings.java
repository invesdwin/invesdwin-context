package de.invesdwin.context.jasperreports;

import java.awt.Dimension;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.context.jfreechart.visitor.AJFreeChartVisitor;
import de.invesdwin.context.jfreechart.visitor.JFreeChartThemeChanger;
import de.invesdwin.util.bean.AValueObject;

@NotThreadSafe
public class JFreeChartExporterSettings extends AValueObject {

    public static final JFreeChartThemeChanger DEFAULT_THEME = new JFreeChartThemeChanger();

    private final Dimension bounds;
    private Double fontMultiplier;
    private AJFreeChartVisitor theme = DEFAULT_THEME;

    public JFreeChartExporterSettings(final Dimension bounds) {
        this.bounds = bounds;
    }

    public Dimension getBounds() {
        return bounds;
    }

    public JFreeChartExporterSettings setFontMultiplier(final Double fontMultiplier) {
        this.fontMultiplier = fontMultiplier;
        return this;
    }

    public Double getFontMultiplier() {
        return fontMultiplier;
    }

    public AJFreeChartVisitor getTheme() {
        return theme;
    }

    public JFreeChartExporterSettings setTheme(final AJFreeChartVisitor theme) {
        this.theme = theme;
        return this;
    }

}
