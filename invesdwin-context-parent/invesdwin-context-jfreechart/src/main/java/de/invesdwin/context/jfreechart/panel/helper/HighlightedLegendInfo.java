package de.invesdwin.context.jfreechart.panel.helper;

import javax.annotation.concurrent.Immutable;

import org.jfree.chart.plot.XYPlot;

import de.invesdwin.context.jfreechart.panel.basis.CustomLegendTitle;

@Immutable
public class HighlightedLegendInfo {

    private final int subplotIndex;
    private final XYPlot plot;
    private final CustomLegendTitle title;
    private final int datasetIndex;

    public HighlightedLegendInfo(final int subplotIndex, final XYPlot plot, final CustomLegendTitle title,
            final int datasetIndex) {
        this.subplotIndex = subplotIndex;
        this.plot = plot;
        this.title = title;
        this.datasetIndex = datasetIndex;
    }

    public int getSubplotIndex() {
        return subplotIndex;
    }

    public XYPlot getPlot() {
        return plot;
    }

    public CustomLegendTitle getTitle() {
        return title;
    }

    public int getDatasetIndex() {
        return datasetIndex;
    }

}
