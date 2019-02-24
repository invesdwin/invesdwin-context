package de.invesdwin.context.jfreechart.plot;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class RangeAxisData {
    private final String rangeAxisId;
    private final int rangeAxisIndex;
    private int precision = 0;
    private boolean visible = false;
    private final Set<Integer> datasetIndexes = new LinkedHashSet<>();

    public RangeAxisData(final String rangeAxisId, final int rangeAxisIndex) {
        this.rangeAxisId = rangeAxisId;
        this.rangeAxisIndex = rangeAxisIndex;
    }

    public String getRangeAxisId() {
        return rangeAxisId;
    }

    public int getRangeAxisIndex() {
        return rangeAxisIndex;
    }

    public int getPrecision() {
        return precision;
    }

    public void setPrecision(final int precision) {
        this.precision = precision;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(final boolean visible) {
        this.visible = visible;
    }

    public Set<Integer> getDatasetIndexes() {
        return datasetIndexes;
    }
}