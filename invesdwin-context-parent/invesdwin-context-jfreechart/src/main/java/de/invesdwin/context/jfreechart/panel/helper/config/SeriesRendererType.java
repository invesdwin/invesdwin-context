package de.invesdwin.context.jfreechart.panel.helper.config;

import java.awt.Color;

import javax.annotation.concurrent.Immutable;

import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYItemRenderer;

import de.invesdwin.context.jfreechart.panel.helper.legend.HighlightedLegendInfo;
import de.invesdwin.context.jfreechart.renderer.DisabledXYItemRenderer;
import de.invesdwin.context.jfreechart.renderer.FastStandardXYItemRenderer;
import de.invesdwin.context.jfreechart.renderer.FastXYAreaRenderer;
import de.invesdwin.context.jfreechart.renderer.FastXYBarRenderer;
import de.invesdwin.context.jfreechart.renderer.FastXYStepRenderer;
import de.invesdwin.context.jfreechart.renderer.custom.ICustomRendererType;
import de.invesdwin.util.error.UnknownArgumentException;

@Immutable
public enum SeriesRendererType implements IRendererType {
    Line {
        @Override
        public XYItemRenderer newRenderer(final LineStyleType lineStyleType, final LineWidthType lineWidthType,
                final Color color) {
            final FastStandardXYItemRenderer renderer = new FastStandardXYItemRenderer();
            renderer.setSeriesPaint(0, color);
            renderer.setSeriesFillPaint(0, color);
            renderer.setSeriesStroke(0, lineStyleType.getStroke(lineWidthType));
            return renderer;
        }

        @Override
        public boolean isLineStyleConfigurable() {
            return true;
        }

        @Override
        public boolean isLineWidthConfigurable() {
            return true;
        }
    },
    Step {
        @Override
        public XYItemRenderer newRenderer(final LineStyleType lineStyleType, final LineWidthType lineWidthType,
                final Color color) {
            final FastXYStepRenderer renderer = new FastXYStepRenderer();
            renderer.setSeriesPaint(0, color);
            renderer.setSeriesFillPaint(0, color);
            renderer.setSeriesStroke(0, lineStyleType.getStroke(lineWidthType));
            return renderer;
        }

        @Override
        public boolean isLineStyleConfigurable() {
            return true;
        }

        @Override
        public boolean isLineWidthConfigurable() {
            return true;
        }
    },
    Area {
        @Override
        public XYItemRenderer newRenderer(final LineStyleType lineStyleType, final LineWidthType lineWidthType,
                final Color color) {
            final FastXYAreaRenderer renderer = new FastXYAreaRenderer();
            renderer.setSeriesPaint(0, color);
            renderer.setSeriesStroke(0, lineStyleType.getStroke(lineWidthType));
            return renderer;
        }

        @Override
        public boolean isLineStyleConfigurable() {
            return true;
        }

        @Override
        public boolean isLineWidthConfigurable() {
            return true;
        }
    },
    Column {
        @Override
        public XYItemRenderer newRenderer(final LineStyleType lineStyleType, final LineWidthType lineWidthType,
                final Color color) {
            final FastXYBarRenderer renderer = new FastXYBarRenderer();
            renderer.setBarPainter(new StandardXYBarPainter());
            renderer.setShadowVisible(false);
            renderer.setSeriesPaint(0, color);
            renderer.setSeriesFillPaint(0, color);
            renderer.setSeriesStroke(0, lineStyleType.getStroke(lineWidthType));
            renderer.setDrawBarOutline(false);
            return renderer;
        }

        @Override
        public boolean isLineStyleConfigurable() {
            return false;
        }

        @Override
        public boolean isLineWidthConfigurable() {
            return false;
        }
    },
    Histogram {
        @Override
        public XYItemRenderer newRenderer(final LineStyleType lineStyleType, final LineWidthType lineWidthType,
                final Color color) {
            final FastXYBarRenderer renderer = new FastXYBarRenderer(HISTOGRAM_MARGIN);
            renderer.setBarPainter(new StandardXYBarPainter());
            renderer.setShadowVisible(false);
            renderer.setSeriesPaint(0, color);
            renderer.setSeriesFillPaint(0, color);
            renderer.setSeriesStroke(0, lineStyleType.getStroke(lineWidthType));
            renderer.setDrawBarOutline(false);
            return renderer;
        }

        @Override
        public boolean isLineStyleConfigurable() {
            return false;
        }

        @Override
        public boolean isLineWidthConfigurable() {
            return false;
        }
    },
    Custom {
        @Override
        public boolean isLineStyleConfigurable() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isLineWidthConfigurable() {
            throw new UnsupportedOperationException();
        }

        @Override
        public XYItemRenderer newRenderer(final LineStyleType lineStyleType, final LineWidthType lineWidthType,
                final Color color) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isSeriesColorConfigurable() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isDownColorConfigurable() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isUpColorConfigurable() {
            throw new UnsupportedOperationException();
        }
    };
    private static final double HISTOGRAM_MARGIN = 0.80D;

    public abstract XYItemRenderer newRenderer(LineStyleType lineStyleType, LineWidthType lineWidthType, Color color);

    public static SeriesRendererType valueOf(final XYItemRenderer renderer) {
        final XYItemRenderer unwrapped = DisabledXYItemRenderer.maybeUnwrap(renderer);
        if (unwrapped instanceof ICustomRendererType) {
            return SeriesRendererType.Custom;
        } else if (unwrapped instanceof FastStandardXYItemRenderer) {
            return SeriesRendererType.Line;
        } else if (unwrapped instanceof FastXYStepRenderer) {
            return SeriesRendererType.Step;
        } else if (unwrapped instanceof FastXYAreaRenderer) {
            return SeriesRendererType.Area;
        } else if (unwrapped instanceof FastXYBarRenderer) {
            final FastXYBarRenderer cRenderer = (FastXYBarRenderer) unwrapped;
            if (cRenderer.getMargin() == HISTOGRAM_MARGIN) {
                return SeriesRendererType.Histogram;
            } else {
                return SeriesRendererType.Column;
            }
        }
        throw UnknownArgumentException.newInstance(Class.class, unwrapped.getClass());
    }

    @Override
    public boolean isUpColorConfigurable() {
        return false;
    }

    @Override
    public boolean isDownColorConfigurable() {
        return false;
    }

    @Override
    public boolean isSeriesColorConfigurable() {
        return true;
    }

    @Override
    public void reset(final HighlightedLegendInfo highlighted, final SeriesInitialSettings initialSettings) {
        final XYItemRenderer newRenderer = newRenderer(initialSettings.getLineStyleType(),
                initialSettings.getLineWidthType(), initialSettings.getSeriesColor());
        highlighted.setRenderer(newRenderer);
    }

    @Override
    public SeriesRendererType getSeriesRendererType() {
        return this;
    }
}
