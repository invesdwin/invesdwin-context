package de.invesdwin.context.jfreechart.panel.helper.config;

import javax.annotation.concurrent.NotThreadSafe;

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
        public boolean isColorConfigurable() {
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
        public boolean isColorConfigurable() {
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
        public boolean isColorConfigurable() {
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
        public boolean isColorConfigurable() {
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
        public boolean isColorConfigurable() {
            return false;
        }
    };

}
