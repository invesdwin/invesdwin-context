package de.invesdwin.context.jfreechart.axis;

import javax.annotation.concurrent.Immutable;

import org.jfree.chart.axis.AxisLocation;

@Immutable
public final class AxisLocations {

    private AxisLocations() {}

    public static boolean isLeft(final AxisLocation location) {
        if (location == AxisLocation.BOTTOM_OR_LEFT) {
            return true;
        } else if (location == AxisLocation.TOP_OR_LEFT) {
            return true;
        }
        return false;
    }

    public static boolean isRight(final AxisLocation location) {
        if (location == AxisLocation.BOTTOM_OR_RIGHT) {
            return true;
        } else if (location == AxisLocation.TOP_OR_RIGHT) {
            return true;
        }
        return false;
    }

    public static boolean isTop(final AxisLocation location) {
        if (location == AxisLocation.BOTTOM_OR_LEFT) {
            return true;
        } else if (location == AxisLocation.BOTTOM_OR_RIGHT) {
            return true;
        }
        return false;
    }

    public static boolean isBottom(final AxisLocation location) {
        if (location == AxisLocation.TOP_OR_LEFT) {
            return true;
        } else if (location == AxisLocation.TOP_OR_RIGHT) {
            return true;
        }
        return false;
    }

}
