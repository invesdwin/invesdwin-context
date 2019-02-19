package de.invesdwin.context.jfreechart.panel.helper.config;

import java.awt.BasicStroke;
import java.awt.Stroke;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.error.UnknownArgumentException;

@Immutable
public enum StrokeType {
    Solid("Solid", new BasicStroke(1.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL)),
    Dotted("Dotted", new BasicStroke(0.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 1, 1 }, 0)),
    Dashed("Dashed", new BasicStroke(0.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 5, 6 }, 0)),
    LargeDashed(
            "Large Dashed",
            new BasicStroke(0.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 10, 6 }, 0)),;

    private String text;
    private Stroke stroke;

    StrokeType(final String text, final Stroke stroke) {
        this.text = text;
        this.stroke = stroke;
    }

    @Override
    public String toString() {
        return text;
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
