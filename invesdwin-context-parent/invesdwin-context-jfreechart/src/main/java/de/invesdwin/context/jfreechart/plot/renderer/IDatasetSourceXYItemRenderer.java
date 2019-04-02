package de.invesdwin.context.jfreechart.plot.renderer;

import org.jfree.chart.renderer.xy.XYItemRenderer;

import de.invesdwin.context.jfreechart.plot.dataset.IPlotSourceDataset;

public interface IDatasetSourceXYItemRenderer extends XYItemRenderer {

    IPlotSourceDataset getDataset();

}
