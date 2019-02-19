package de.invesdwin.context.jfreechart.panel.helper;

import java.awt.BasicStroke;
import java.awt.Stroke;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.error.UnknownArgumentException;

@Immutable
public enum StrokeType {
    Solid(new BasicStroke(1.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL)),
    Dotted(new BasicStroke(0.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 1, 1 }, 0)),
    Dashed(new BasicStroke(0.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 5, 6 }, 0)),
    LargeDashed(new BasicStroke(0.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 10, 6 }, 0)),;

    private Stroke stroke;

    StrokeType(final Stroke stroke) {
        this.stroke = stroke;
    }

    public Stroke getStroke() {
        return stroke;
    }

    public static StrokeType valueOf(final Stroke stroke) {
        for (final StrokeType type : values()) {
            if (type.stroke == stroke) {
                return type;
            }
        }
        throw UnknownArgumentException.newInstance(Stroke.class, stroke);
    }
}
