package de.invesdwin.context.jfreechart.panel.helper.config;

import java.awt.BasicStroke;
import java.awt.Stroke;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.error.UnknownArgumentException;

@Immutable
public enum LineWidthType {
    _1(1),
    _2(2),
    _3(3),
    _4(4);

    private int width;

    LineWidthType(final int width) {
        this.width = width;
    }

    public int getWidth() {
        return width;
    }

    @Override
    public String toString() {
        return String.valueOf(width);
    }

    public static LineWidthType valueOf(final Stroke stroke) {
        if (stroke instanceof BasicStroke) {
            final BasicStroke cStroke = (BasicStroke) stroke;
            return valueOf(cStroke.getLineWidth());
        }
        throw UnknownArgumentException.newInstance(Stroke.class, stroke);
    }

    public static LineWidthType valueOf(final Number width) {
        return valueOf(width.intValue());
    }

    public static LineWidthType valueOf(final int width) {
        switch (width) {
        case 1:
            return _1;
        case 2:
            return _2;
        case 3:
            return _3;
        case 4:
            return _4;
        default:
            throw UnknownArgumentException.newInstance(Integer.class, width);
        }
    }

    public Stroke getStroke(final Stroke stroke) {
        final LineStyleType lineStyleType = LineStyleType.valueOf(stroke);
        return lineStyleType.getStroke(this);
    }
}
