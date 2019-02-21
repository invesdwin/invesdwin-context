package de.invesdwin.context.jfreechart.panel.helper.config;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.context.jfreechart.panel.helper.legend.HighlightedLegendInfo;

@NotThreadSafe
public enum PriceRendererType implements IRendererType {
    Line {
        @Override
        public boolean isLineStyleConfigurable() {
            return true;
        }

        @Override
        public boolean isLineWidthConfigurable() {
            return true;
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
        public boolean isPriceColorConfigurable() {
            return true;
        }
    },
    Step {
        @Override
        public boolean isLineStyleConfigurable() {
            return true;
        }

        @Override
        public boolean isLineWidthConfigurable() {
            return true;
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
        public boolean isPriceColorConfigurable() {
            return true;
        }
    },
    Area {
        @Override
        public boolean isLineStyleConfigurable() {
            return true;
        }

        @Override
        public boolean isLineWidthConfigurable() {
            return true;
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
        public boolean isPriceColorConfigurable() {
            return true;
        }
    },
    OHLC {
        @Override
        public boolean isLineStyleConfigurable() {
            return false;
        }

        @Override
        public boolean isLineWidthConfigurable() {
            return true;
        }

        @Override
        public boolean isUpColorConfigurable() {
            return true;
        }

        @Override
        public boolean isDownColorConfigurable() {
            return true;
        }

        @Override
        public boolean isPriceColorConfigurable() {
            return false;
        }
    },
    Candlestick {
        @Override
        public boolean isLineStyleConfigurable() {
            return false;
        }

        @Override
        public boolean isLineWidthConfigurable() {
            return true;
        }

        @Override
        public boolean isUpColorConfigurable() {
            return true;
        }

        @Override
        public boolean isDownColorConfigurable() {
            return true;
        }

        @Override
        public boolean isPriceColorConfigurable() {
            return false;
        }
    };

    @Override
    public void reset(final HighlightedLegendInfo highlighted, final InitialSeriesSettings initialSettings) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SeriesRendererType getSeriesRendererType() {
        throw new UnsupportedOperationException();
    }

}
