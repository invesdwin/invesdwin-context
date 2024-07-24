package de.invesdwin.context.jasperreports;

import java.awt.Color;
import java.awt.Paint;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.context.jfreechart.visitor.JFreeChartThemeSwing;
import de.invesdwin.util.lang.color.Colors;

@Immutable
public class JFreeChartThemeDocument extends JFreeChartThemeSwing {

    public static final boolean DEFAULT_OUTLINE_VISIBLE = false;
    public static final Color DEFAULT_GRID_PAINT = Colors.fromHex("F2F2F2");

    @Override
    protected Paint getGridlinePaint() {
        return DEFAULT_GRID_PAINT;
    }

    @Override
    protected boolean isOutlineVisible() {
        return DEFAULT_OUTLINE_VISIBLE;
    }

}
