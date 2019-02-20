package de.invesdwin.context.jfreechart.panel.helper.config;

import java.awt.BasicStroke;
import java.awt.Stroke;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.error.UnknownArgumentException;

@Immutable
public enum LineStyleType {
    Solid("Solid") {
        @Override
        protected Stroke newStroke(final float width) {
            return new BasicStroke(width, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL);
        }
    },
    Dotted("Dotted") {
        @Override
        protected Stroke newStroke(final float width) {
            return new BasicStroke(width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 3.0f, 3.0f },
                    0.0f);
        }
    },
    Dashed("Dashed") {
        @Override
        protected Stroke newStroke(final float width) {
            return new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f,
                    new float[] { 6.0f, 6.0f }, 0.0f);
        }
    },
    LargeDashed("Large Dashed") {
        @Override
        protected Stroke newStroke(final float width) {
            return new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f,
                    new float[] { 9.0f, 9.0f }, 0.0f);
        }
    };

    private String text;
    private final Map<LineWidthType, Stroke> width_stroke = new HashMap<>();

    LineStyleType(final String text) {
        this.text = text;
        for (final LineWidthType width : LineWidthType.values()) {
            width_stroke.put(width, newStroke(width.getWidth()));
        }
    }

    @Override
    public String toString() {
        return text;
    }

    public Stroke getStroke(final Stroke stroke) {
        return width_stroke.get(LineWidthType.valueOf(stroke));
    }

    public Stroke getStroke(final LineWidthType lineWidth) {
        return width_stroke.get(lineWidth);
    }

    protected abstract Stroke newStroke(float width);

    public static LineStyleType valueOf(final Stroke stroke) {
        for (final LineStyleType type : values()) {
            if (type.width_stroke.containsValue(stroke)) {
                return type;
            }
        }
        throw UnknownArgumentException.newInstance(Stroke.class, stroke);
    }
}
