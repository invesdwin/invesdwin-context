package de.invesdwin.context.jfreechart.panel.helper.config;

import java.awt.Color;

import javax.annotation.concurrent.Immutable;

import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYStepRenderer;

import de.invesdwin.context.jfreechart.renderer.CustomXYAreaRenderer;
import de.invesdwin.context.jfreechart.renderer.DisabledXYItemRenderer;
import de.invesdwin.util.error.UnknownArgumentException;

@Immutable
public enum SeriesRendererType implements IRendererType {
    Line {
        @Override
        public XYItemRenderer newRenderer(final StrokeType strokeType, final Color color) {
            final StandardXYItemRenderer renderer = new StandardXYItemRenderer();
            renderer.setSeriesPaint(0, color);
            renderer.setSeriesFillPaint(0, color);
            renderer.setSeriesStroke(0, strokeType.getStroke());
            return renderer;
        }

        @Override
        public boolean isStrokeConfigurable() {
            return true;
        }
    },
    Step {
        @Override
        public XYItemRenderer newRenderer(final StrokeType strokeType, final Color color) {
            final XYStepRenderer renderer = new XYStepRenderer();
            renderer.setSeriesPaint(0, color);
            renderer.setSeriesFillPaint(0, color);
            renderer.setSeriesStroke(0, strokeType.getStroke());
            return renderer;
        }

        @Override
        public boolean isStrokeConfigurable() {
            return true;
        }
    },
    Area {
        @Override
        public XYItemRenderer newRenderer(final StrokeType strokeType, final Color color) {
            final CustomXYAreaRenderer renderer = new CustomXYAreaRenderer();
            renderer.setSeriesPaint(0, color);
            final Color colorAlpha = new Color(color.getRed(), color.getGreen(), color.getBlue(),
                    PlotConfigurationHelper.AREA_FILL_ALPHA);
            renderer.setSeriesFillPaint(0, colorAlpha);
            renderer.setSeriesStroke(0, strokeType.getStroke());
            return renderer;
        }

        @Override
        public boolean isStrokeConfigurable() {
            return true;
        }
    },
    Column {
        @Override
        public XYItemRenderer newRenderer(final StrokeType strokeType, final Color color) {
            final XYBarRenderer renderer = new XYBarRenderer();
            renderer.setBarPainter(new StandardXYBarPainter());
            renderer.setShadowVisible(false);
            renderer.setSeriesPaint(0, color);
            renderer.setSeriesFillPaint(0, color);
            renderer.setSeriesStroke(0, strokeType.getStroke());
            renderer.setDrawBarOutline(false);
            return renderer;
        }

        @Override
        public boolean isStrokeConfigurable() {
            return false;
        }
    },
    Histogram {
        @Override
        public XYItemRenderer newRenderer(final StrokeType strokeType, final Color color) {
            final XYBarRenderer renderer = new XYBarRenderer(HISTOGRAM_MARGIN);
            renderer.setBarPainter(new StandardXYBarPainter());
            renderer.setShadowVisible(false);
            renderer.setSeriesPaint(0, color);
            renderer.setSeriesFillPaint(0, color);
            renderer.setSeriesStroke(0, strokeType.getStroke());
            renderer.setDrawBarOutline(false);
            return renderer;
        }

        @Override
        public boolean isStrokeConfigurable() {
            return false;
        }
    };
    private static final double HISTOGRAM_MARGIN = 0.80D;

    public abstract XYItemRenderer newRenderer(StrokeType strokeType, Color color);

    public static SeriesRendererType valueOf(final XYItemRenderer renderer) {
        final XYItemRenderer unwrapped = DisabledXYItemRenderer.maybeUnwrap(renderer);
        if (unwrapped instanceof StandardXYItemRenderer) {
            return SeriesRendererType.Line;
        } else if (unwrapped instanceof XYStepRenderer) {
            return SeriesRendererType.Step;
        } else if (unwrapped instanceof CustomXYAreaRenderer) {
            return SeriesRendererType.Area;
        } else if (unwrapped instanceof XYBarRenderer) {
            final XYBarRenderer cRenderer = (XYBarRenderer) unwrapped;
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
    public boolean isColorConfigurable() {
        return true;
    }
}
