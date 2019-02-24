package de.invesdwin.context.jfreechart.panel.helper.legend;

import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

import javax.annotation.concurrent.NotThreadSafe;

import org.jfree.chart.LegendItem;
import org.jfree.chart.annotations.XYAnnotation;
import org.jfree.chart.annotations.XYTitleAnnotation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.LegendItemEntity;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.ui.RectangleAnchor;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.data.general.Dataset;
import org.jfree.data.xy.OHLCDataset;
import org.jfree.data.xy.XYDataset;

import de.invesdwin.aspects.EventDispatchThreadUtil;
import de.invesdwin.context.jfreechart.dataset.DisabledXYDataset;
import de.invesdwin.context.jfreechart.dataset.IPlotSource;
import de.invesdwin.context.jfreechart.icon.XYIconAnnotation;
import de.invesdwin.context.jfreechart.panel.InteractiveChartPanel;
import de.invesdwin.context.jfreechart.panel.basis.CustomChartPanel;
import de.invesdwin.context.jfreechart.panel.basis.CustomCombinedDomainXYPlot;
import de.invesdwin.context.jfreechart.panel.basis.CustomLegendTitle;
import de.invesdwin.context.jfreechart.panel.helper.icons.PlotIcons;
import de.invesdwin.context.jfreechart.plot.XYPlots;
import de.invesdwin.context.jfreechart.renderer.DisabledXYItemRenderer;
import de.invesdwin.util.error.UnknownArgumentException;

@NotThreadSafe
public class PlotLegendHelper {

    private static final Color DEFAULT_BACKGROUND_COLOR = CustomCombinedDomainXYPlot.DEFAULT_BACKGROUND_COLOR;
    private static final Color ADD_BACKGROUND_COLOR = new Color(223, 235, 209);
    private static final Color REMOVE_BACKGROUND_COLOR = new Color(235, 209, 210);
    private static final int INITIAL_PLOT_WEIGHT = CustomCombinedDomainXYPlot.INITIAL_PLOT_WEIGHT;
    private static final int EMPTY_PLOT_WEIGHT = CustomCombinedDomainXYPlot.EMPTY_PLOT_WEIGHT;

    private static final Color LEGEND_BACKGROUND_PAINT = new Color(255, 255, 255, 100);
    private static final Color HIGHLIGHTED_LEGEND_BACKGROUND_PAINT = new Color(222, 222, 222, 100);

    private final InteractiveChartPanel chartPanel;

    private HighlightedLegendInfo highlightedLegendInfo;
    private HighlightedLegendInfo dragStart;
    private boolean dragged = false;
    private XYPlot emptyPlot;
    private XYPlot trashPlot;

    private final XYIconAnnotation addAnnotation;
    private final XYIconAnnotation removeAnnotation;
    private final XYIconAnnotation trashAnnotation;

    private final Set<Dataset> nonRemovableDatasets = Collections
            .newSetFromMap(new IdentityHashMap<Dataset, Boolean>());

    public PlotLegendHelper(final InteractiveChartPanel chartPanel) {
        this.chartPanel = chartPanel;

        addAnnotation = new XYIconAnnotation(0.5D, 0.5D, PlotIcons.ADD.newIcon(16, 0.3F));
        removeAnnotation = new XYIconAnnotation(0.5D, 0.5D, PlotIcons.REMOVE.newIcon(16, 0.3F));
        trashAnnotation = new XYIconAnnotation(0.5D, 0.5D, PlotIcons.TRASH.newIcon(16, 0.3F));
    }

    public void update() {
        final List<XYPlot> plots = chartPanel.getCombinedPlot().getSubplots();
        for (final XYPlot plot : plots) {
            final CustomLegendTitle title = getTitle(plot);
            title.setBackgroundPaint(title.getBackgroundPaint()); //fire change event
        }
    }

    public void addLegendAnnotation(final XYPlot plot) {
        final CustomLegendTitle lt = new CustomLegendTitle(plot) {
            @Override
            protected String newLabel(final LegendItem item) {
                final String label = super.newLabel(item);
                int domainMarkerItem = (int) chartPanel.getPlotCrosshairHelper().getDomainCrosshairMarker().getValue();
                if (domainMarkerItem == -1) {
                    domainMarkerItem = chartPanel.getDataset().getData().size() - 1;
                }
                if (domainMarkerItem >= 0) {
                    final Dataset dataset = item.getDataset();
                    if (dataset instanceof DisabledXYDataset) {
                        return label;
                    }
                    final IPlotSource plotSource = (IPlotSource) dataset;
                    final XYPlot plot = plotSource.getPlot();
                    if (plot == chartPanel.getCombinedPlot().getTrashPlot()) {
                        return label;
                    }
                    final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxisForDataset(item.getDatasetIndex());
                    final NumberFormat rangeAxisFormat = rangeAxis.getNumberFormatOverride();
                    if (dataset instanceof OHLCDataset) {
                        final OHLCDataset ohlc = (OHLCDataset) dataset;
                        if (domainMarkerItem >= ohlc.getItemCount(0)) {
                            domainMarkerItem = ohlc.getItemCount(0) - 1;
                        }
                        final StringBuilder sb = new StringBuilder(label);
                        sb.append(" O:");
                        sb.append(rangeAxisFormat.format(ohlc.getOpen(0, domainMarkerItem)));
                        sb.append(" H:");
                        sb.append(rangeAxisFormat.format(ohlc.getHigh(0, domainMarkerItem)));
                        sb.append(" L:");
                        sb.append(rangeAxisFormat.format(ohlc.getLow(0, domainMarkerItem)));
                        sb.append(" C:");
                        sb.append(rangeAxisFormat.format(ohlc.getClose(0, domainMarkerItem)));
                        sb.append(" T:");
                        sb.append(chartPanel.getDomainAxisFormat().format(domainMarkerItem));
                        return sb.toString();
                    } else if (dataset instanceof XYDataset) {
                        final XYDataset xy = (XYDataset) dataset;
                        if (domainMarkerItem >= xy.getItemCount(0)) {
                            domainMarkerItem = xy.getItemCount(0) - 1;
                        }
                        final StringBuilder sb = new StringBuilder(label);
                        sb.append(" ");
                        sb.append(rangeAxisFormat.format(xy.getYValue(0, domainMarkerItem)));
                        return sb.toString();
                    } else {
                        throw UnknownArgumentException.newInstance(Class.class, dataset.getClass());
                    }
                } else {
                    return label;
                }
            }

            @Override
            protected Font newTextFont(final LegendItem item, final Font textFont) {
                if (highlightedLegendInfo != null
                        && highlightedLegendInfo.getDatasetIndex() == item.getDatasetIndex()) {
                    final IPlotSource plotSource = (IPlotSource) item.getDataset();
                    if (highlightedLegendInfo.getPlot() == plotSource.getPlot()) {
                        return textFont.deriveFont(Font.ITALIC);
                    }
                }
                return super.newTextFont(item, textFont);
            }

            @Override
            protected Paint newFillPaint(final LegendItem item) {
                int domainMarkerItem = (int) chartPanel.getPlotCrosshairHelper().getDomainCrosshairMarker().getValue();
                if (domainMarkerItem == -1) {
                    domainMarkerItem = chartPanel.getDataset().getData().size() - 1;
                }
                if (domainMarkerItem >= 0) {
                    final Dataset dataset = item.getDataset();
                    final IPlotSource plotSource = (IPlotSource) dataset;
                    final int datasetIndex = item.getDatasetIndex();
                    final XYItemRenderer renderer = plotSource.getPlot().getRenderer(datasetIndex);
                    if (renderer == null) {
                        return super.newFillPaint(item);
                    } else if (dataset instanceof OHLCDataset) {
                        final OHLCDataset ohlc = (OHLCDataset) dataset;
                        if (domainMarkerItem >= ohlc.getItemCount(0)) {
                            domainMarkerItem = ohlc.getItemCount(0) - 1;
                        }
                        return renderer.getItemPaint(0, domainMarkerItem);
                    } else if (dataset instanceof XYDataset) {
                        final XYDataset xy = (XYDataset) dataset;
                        if (domainMarkerItem >= xy.getItemCount(0)) {
                            domainMarkerItem = xy.getItemCount(0) - 1;
                        }
                        return renderer.getItemPaint(0, domainMarkerItem);
                    } else {
                        throw UnknownArgumentException.newInstance(Class.class, dataset.getClass());
                    }
                } else {
                    return super.newFillPaint(item);
                }
            }
        };
        addLegendAnnotation(plot, lt);
    }

    private void addLegendAnnotation(final XYPlot plot, final CustomLegendTitle lt) {
        lt.setItemFont(new Font("Dialog", Font.PLAIN, 9));
        lt.setBackgroundPaint(LEGEND_BACKGROUND_PAINT);
        lt.setPosition(RectangleEdge.TOP);
        final XYTitleAnnotation ta = new XYTitleAnnotation(0.005, 0.99, lt, RectangleAnchor.TOP_LEFT);
        plot.addAnnotation(ta);
    }

    private void highlightLegendInfo(final int mouseX, final int mouseY) {
        final ChartEntity entityForPoint = chartPanel.getChartPanel().getEntityForPoint(mouseX, mouseY);
        boolean updated = false;
        if (entityForPoint instanceof LegendItemEntity) {
            final LegendItemEntity l = (LegendItemEntity) entityForPoint;
            final Dataset dataset = l.getDataset();
            final IPlotSource plotSource = (IPlotSource) dataset;
            final XYPlot plot = plotSource.getPlot();
            final CustomLegendTitle title = getTitle(plot);
            final int datasetIndex = XYPlots.getDatasetIndexForDataset(plot, dataset);
            if (highlightedLegendInfo == null || highlightedLegendInfo.getTitle() != title
                    || highlightedLegendInfo.getDatasetIndex() != datasetIndex) {
                //CHECKSTYLE:OFF
                if (highlightedLegendInfo != null) {
                    //CHECKSTYLE:ON
                    highlightedLegendInfo.getTitle().setBackgroundPaint(LEGEND_BACKGROUND_PAINT);
                }
                final int subplotIndex = chartPanel.getCombinedPlot().getSubplotIndex(mouseX, mouseY);
                highlightedLegendInfo = new HighlightedLegendInfo(chartPanel, subplotIndex, plot, title, datasetIndex);
                title.setBackgroundPaint(HIGHLIGHTED_LEGEND_BACKGROUND_PAINT);
                chartPanel.getChartPanel().setCursor(CustomChartPanel.DEFAULT_CURSOR);
            }
            updated = true;
        }
        if (!updated) {
            disableHighlighting();
        }
    }

    public void disableHighlighting() {
        if (!dragged && highlightedLegendInfo != null) {
            highlightedLegendInfo.getTitle().setBackgroundPaint(LEGEND_BACKGROUND_PAINT);
            highlightedLegendInfo = null;
        }
    }

    public void mouseReleased(final MouseEvent e) {
        mouseMoved(e);
        if (e.getButton() != MouseEvent.BUTTON1) {
            return;
        }
        if (!dragged) {
            toggleDatasetVisibility(e);
        }
        if (dragStart != null) {
            dragStart = null;
            dragged = false;
            chartPanel.getChartPanel().setCursor(CustomChartPanel.DEFAULT_CURSOR);
            if (trashPlot != null) {
                for (int datasetIndex = 0; datasetIndex < trashPlot.getDatasetCount(); datasetIndex++) {
                    final XYDataset dataset = trashPlot.getDataset(datasetIndex);
                    if (dataset != null) {
                        final String seriesKey = String.valueOf(dataset.getSeriesKey(0));
                        chartPanel.getPlotConfigurationHelper().removeInitialSeriesSettings(seriesKey);
                    }
                }
                trashPlot = null;
            }
            if (emptyPlot != null) {
                chartPanel.getCombinedPlot().removeEmptyPlotsAndResetTrashPlot();
                emptyPlot = null;
            }
        }
    }

    private void toggleDatasetVisibility(final MouseEvent e) {
        final int mouseX = e.getX();
        final int mouseY = e.getY();
        final ChartEntity entityForPoint = chartPanel.getChartPanel().getEntityForPoint(mouseX, mouseY);
        if (entityForPoint instanceof LegendItemEntity) {
            final LegendItemEntity l = (LegendItemEntity) entityForPoint;
            final XYDataset dataset = (XYDataset) l.getDataset();
            if (dataset instanceof IPlotSource) {
                final IPlotSource plotSource = (IPlotSource) dataset;
                final XYPlot plot = plotSource.getPlot();
                final int datasetIndex = XYPlots.getDatasetIndexForDataset(plot, dataset);
                final XYItemRenderer renderer = plot.getRenderer(datasetIndex);
                setDatasetHidden(plot, datasetIndex, !(renderer instanceof DisabledXYItemRenderer));
                EventDispatchThreadUtil.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        highlightLegendInfo(mouseX, mouseY); //update highlighting
                        chartPanel.update(); //force repaint
                    }
                });
            }
        }
    }

    public void setDatasetHidden(final XYPlot plot, final int datasetIndex, final boolean hidden) {
        final XYDataset dataset = plot.getDataset(datasetIndex);
        final XYItemRenderer renderer = plot.getRenderer(datasetIndex);
        if (!hidden) {
            final DisabledXYDataset cDataset = (DisabledXYDataset) dataset;
            plot.setDataset(datasetIndex, cDataset.getEnabledDataset());
            final DisabledXYItemRenderer cRenderer = (DisabledXYItemRenderer) renderer;
            plot.setRenderer(datasetIndex, cRenderer.getEnabledRenderer());
        } else {
            plot.setDataset(datasetIndex, new DisabledXYDataset(dataset));
            plot.setRenderer(datasetIndex, new DisabledXYItemRenderer(renderer));
        }
        XYPlots.updateRangeAxes(plot);
    }

    public void mousePressed(final MouseEvent e) {
        mouseMoved(e);
        if (e.getButton() != MouseEvent.BUTTON1) {
            return;
        }
        if (highlightedLegendInfo != null) {
            if (dragStart != highlightedLegendInfo) {
                dragStart = highlightedLegendInfo;
            }
        }
    }

    public void mouseMoved(final MouseEvent e) {
        final int mouseX = e.getX();
        final int mouseY = e.getY();
        highlightLegendInfo(mouseX, mouseY);
    }

    public void mouseDragged(final MouseEvent e) {
        if (dragStart != null) {
            final int mouseX = e.getX();
            final int mouseY = e.getY();
            final int toSubplotIndex = chartPanel.getCombinedPlot().getSubplotIndex(mouseX, mouseY);
            if (!dragged) {
                dragged = true;
                emptyPlot = chartPanel.newPlot();
                emptyPlot.addAnnotation(addAnnotation);
                emptyPlot.setBackgroundPaint(ADD_BACKGROUND_COLOR);
                chartPanel.getCombinedPlot().add(emptyPlot, EMPTY_PLOT_WEIGHT);

                final XYDataset dataset = dragStart.getPlot().getDataset(dragStart.getDatasetIndex());
                if (isDatasetRemovable(dataset)) {
                    trashPlot = chartPanel.getCombinedPlot().getTrashPlot();
                    trashPlot.addAnnotation(trashAnnotation);
                    trashPlot.setBackgroundPaint(REMOVE_BACKGROUND_COLOR);
                    trashPlot.setWeight(EMPTY_PLOT_WEIGHT);
                }
            }
            chartPanel.getChartPanel().setCursor(CustomChartPanel.MOVE_CURSOR);
            if (toSubplotIndex != -1 && toSubplotIndex != dragStart.getSubplotIndex()) {
                final XYPlot fromPlot = dragStart.getPlot();
                final List<XYPlot> toPlots = chartPanel.getCombinedPlot().getSubplots();
                final XYPlot toPlot = toPlots.get(toSubplotIndex);
                final int fromDatasetIndex = dragStart.getDatasetIndex();
                final int toDatasetIndex = XYPlots.getFreeDatasetIndex(toPlot);
                final XYDataset dataset = fromPlot.getDataset(fromDatasetIndex);
                final IPlotSource plotSource = (IPlotSource) dataset;
                plotSource.setPlot(toPlot);
                toPlot.setDataset(toDatasetIndex, dataset);
                toPlot.setRenderer(toDatasetIndex, fromPlot.getRenderer(fromDatasetIndex));
                XYPlots.removeDataset(fromPlot, fromDatasetIndex);
                updatePlots(fromPlot, toPlot);
                highlightedLegendInfo.getTitle().setBackgroundPaint(LEGEND_BACKGROUND_PAINT);
                dragStart = new HighlightedLegendInfo(chartPanel, toSubplotIndex, toPlot, getTitle(toPlot),
                        toDatasetIndex);
                dragStart.getTitle().setBackgroundPaint(HIGHLIGHTED_LEGEND_BACKGROUND_PAINT);
                highlightedLegendInfo = dragStart;
            }
        }
    }

    private void updatePlots(final XYPlot fromPlot, final XYPlot toPlot) {
        if (toPlot == emptyPlot) {
            emptyPlot.setWeight(INITIAL_PLOT_WEIGHT);
            emptyPlot.removeAnnotation(addAnnotation);
            emptyPlot.setBackgroundPaint(DEFAULT_BACKGROUND_COLOR);
        } else {
            emptyPlot.setWeight(EMPTY_PLOT_WEIGHT);
            if (!emptyPlot.getAnnotations().contains(addAnnotation)) {
                emptyPlot.addAnnotation(addAnnotation);
                emptyPlot.setBackgroundPaint(ADD_BACKGROUND_COLOR);
            }
        }
        if (!XYPlots.hasDataset(fromPlot) && fromPlot != emptyPlot && fromPlot != trashPlot) {
            fromPlot.addAnnotation(removeAnnotation);
            fromPlot.setBackgroundPaint(REMOVE_BACKGROUND_COLOR);
        }
        if (toPlot != trashPlot) {
            toPlot.removeAnnotation(removeAnnotation);
            toPlot.setBackgroundPaint(DEFAULT_BACKGROUND_COLOR);
        }

        if (fromPlot != trashPlot) {
            XYPlots.updateRangeAxes(fromPlot);
        }
        if (toPlot != trashPlot) {
            XYPlots.updateRangeAxes(toPlot);
        }
    }

    @SuppressWarnings("unchecked")
    private CustomLegendTitle getTitle(final XYPlot plot) {
        final List<XYAnnotation> annotations = plot.getAnnotations();
        for (final XYAnnotation annotation : annotations) {
            if (annotation instanceof XYTitleAnnotation) {
                final XYTitleAnnotation titleAnnotation = (XYTitleAnnotation) annotation;
                final CustomLegendTitle title = (CustomLegendTitle) titleAnnotation.getTitle();
                return title;
            }
        }
        throw new IllegalStateException("title not found");
    }

    public boolean isHighlighting() {
        return dragStart != null || highlightedLegendInfo != null;
    }

    public HighlightedLegendInfo getHighlightedLegendInfo() {
        if (dragStart != null) {
            return null;
        }
        return highlightedLegendInfo;
    }

    public void setDatasetRemovable(final Dataset dataset, final boolean removable) {
        if (dataset instanceof DisabledXYDataset) {
            throw new IllegalArgumentException(
                    "dataset should not be an instance of " + DisabledXYDataset.class.getSimpleName());
        }
        if (removable) {
            nonRemovableDatasets.remove(dataset);
        } else {
            nonRemovableDatasets.add(dataset);
        }
    }

    public boolean isDatasetRemovable(final XYDataset dataset) {
        return !nonRemovableDatasets.contains(DisabledXYDataset.maybeUnwrap(dataset));
    }

}
