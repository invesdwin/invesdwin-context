package de.invesdwin.context.jfreechart.panel.helper.config;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public enum PriceRendererType implements IRendererType {
    Candlesticks {
        @Override
        public boolean isStrokeConfigurable() {
            return false;
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
    Bars {
        @Override
        public boolean isStrokeConfigurable() {
            return false;
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
    Line {
        @Override
        public boolean isStrokeConfigurable() {
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
        public boolean isStrokeConfigurable() {
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
        public boolean isStrokeConfigurable() {
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
    };

}
