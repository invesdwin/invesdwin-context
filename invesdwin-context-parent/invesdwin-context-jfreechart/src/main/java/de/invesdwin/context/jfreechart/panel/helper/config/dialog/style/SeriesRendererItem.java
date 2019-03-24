package de.invesdwin.context.jfreechart.panel.helper.config.dialog.style;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.context.jfreechart.panel.helper.config.SeriesRendererType;

@Immutable
public final class SeriesRendererItem {
    private final SeriesRendererType type;
    private final String name;

    public SeriesRendererItem(final SeriesRendererType type, final String name) {
        this.type = type;
        this.name = name;
    }

    public SeriesRendererType getType() {
        return type;
    }

    @Override
    public String toString() {
        return name;
    }
}