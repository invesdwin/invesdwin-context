package de.invesdwin.context.jfreechart.panel.helper.config;

import java.awt.Color;

import javax.annotation.concurrent.Immutable;

import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYDataset;

import de.invesdwin.context.jfreechart.panel.helper.legend.HighlightedLegendInfo;
import de.invesdwin.context.jfreechart.plot.renderer.DisabledXYItemRenderer;
import de.invesdwin.context.jfreechart.plot.renderer.FastStandardXYItemRenderer;
import de.invesdwin.context.jfreechart.plot.renderer.FastXYAreaRenderer;
import de.invesdwin.context.jfreechart.plot.renderer.FastXYBarRenderer;
import de.invesdwin.context.jfreechart.plot.renderer.FastXYStepRenderer;
import de.invesdwin.context.jfreechart.plot.renderer.custom.ICustomRendererType;
import de.invesdwin.util.error.UnknownArgumentException;

@Immutable
public enum SeriesRendererType implements IRendererType {
    Line {
        @Override
        public XYItemRenderer newRenderer(final XYDataset dataset, final LineStyleType lineStyleType,
                final LineWidthType lineWidthType, final Color color, final boolean priceLineVisible) {
            final FastStandardXYItemRenderer renderer = new FastStandardXYItemRenderer(dataset);
            renderer.setSeriesPaint(0, color);
            renderer.setSeriesFillPaint(0, color);
            renderer.setSeriesStroke(0, lineStyleType.getStroke(lineWidthType));
            renderer.setPriceLineVisible(priceLineVisible);
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
        public XYItemRenderer newRenderer(final XYDataset dataset, final LineStyleType lineStyleType,
                final LineWidthType lineWidthType, final Color color, final boolean priceLineVisible) {
            final FastXYStepRenderer renderer = new FastXYStepRenderer(dataset);
            renderer.setSeriesPaint(0, color);
            renderer.setSeriesFillPaint(0, color);
            renderer.setSeriesStroke(0, lineStyleType.getStroke(lineWidthType));
            renderer.setPriceLineVisible(priceLineVisible);
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
        public XYItemRenderer newRenderer(final XYDataset dataset, final LineStyleType lineStyleType,
                final LineWidthType lineWidthType, final Color color, final boolean priceLineVisible) {
            final FastXYAreaRenderer renderer = new FastXYAreaRenderer(dataset);
            renderer.setSeriesPaint(0, color);
            renderer.setSeriesStroke(0, lineStyleType.getStroke(lineWidthType));
            renderer.setPriceLineVisible(priceLineVisible);
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
        public XYItemRenderer newRenderer(final XYDataset dataset, final LineStyleType lineStyleType,
                final LineWidthType lineWidthType, final Color color, final boolean priceLineVisible) {
            final FastXYBarRenderer renderer = new FastXYBarRenderer(dataset);
            renderer.setBarPainter(new StandardXYBarPainter());
            renderer.setShadowVisible(false);
            renderer.setSeriesPaint(0, color);
            renderer.setSeriesFillPaint(0, color);
            renderer.setSeriesStroke(0, lineStyleType.getStroke(lineWidthType));
            renderer.setDrawBarOutline(false);
            renderer.setPriceLineVisible(priceLineVisible);
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
        public XYItemRenderer newRenderer(final XYDataset dataset, final LineStyleType lineStyleType,
                final LineWidthType lineWidthType, final Color color, final boolean priceLineVisible) {
            final FastXYBarRenderer renderer = new FastXYBarRenderer(dataset, HISTOGRAM_MARGIN);
            renderer.setBarPainter(new StandardXYBarPainter());
            renderer.setShadowVisible(false);
            renderer.setSeriesPaint(0, color);
            renderer.setSeriesFillPaint(0, color);
            renderer.setSeriesStroke(0, lineStyleType.getStroke(lineWidthType));
            renderer.setDrawBarOutline(false);
            renderer.setPriceLineVisible(priceLineVisible);
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
        public XYItemRenderer newRenderer(final XYDataset dataset, final LineStyleType lineStyleType,
                final LineWidthType lineWidthType, final Color color, final boolean priceLineVisible) {
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

    public abstract XYItemRenderer newRenderer(XYDataset dataset, LineStyleType lineStyleType,
            LineWidthType lineWidthType, Color color, boolean priceLineVisible);

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
        final XYItemRenderer newRenderer = newRenderer(highlighted.getDataset(), initialSettings.getLineStyleType(),
                initialSettings.getLineWidthType(), initialSettings.getSeriesColor(),
                initialSettings.isPriceLineVisible());
        highlighted.setRenderer(newRenderer);
    }

    @Override
    public SeriesRendererType getSeriesRendererType() {
        return this;
    }

    @Override
    public boolean isSeriesRendererTypeConfigurable() {
        return true;
    }

    @Override
    public boolean isPriceLineConfigurable() {
        return true;
    }

}
