package de.invesdwin.context.jfreechart.panel;

import java.awt.Cursor;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.Range;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.general.DatasetChangeListener;

import de.invesdwin.context.jfreechart.dataset.IndexedDateTimeNumberFormat;
import de.invesdwin.context.jfreechart.dataset.IndexedDateTimeOHLCDataset;
import de.invesdwin.context.jfreechart.listener.ChartMouseListenerSupport;
import de.invesdwin.context.jfreechart.panel.basis.CustomChartPanel;
import de.invesdwin.context.jfreechart.panel.basis.CustomCombinedDomainXYPlot;
import de.invesdwin.context.jfreechart.panel.helper.PlotConfigurationHelper;
import de.invesdwin.context.jfreechart.panel.helper.PlotCrosshairHelper;
import de.invesdwin.context.jfreechart.panel.helper.PlotLegendHelper;
import de.invesdwin.context.jfreechart.panel.helper.PlotNavigationHelper;
import de.invesdwin.context.jfreechart.panel.helper.PlotResizeHelper;
import de.invesdwin.context.jfreechart.plot.XYPlots;
import de.invesdwin.context.jfreechart.renderer.CustomCandlestickRenderer;
import de.invesdwin.context.jfreechart.visitor.JFreeChartLocaleChanger;
import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.time.duration.Duration;
import de.invesdwin.util.time.fdate.FDate;
import de.invesdwin.util.time.fdate.FTimeUnit;

// CHECKSTYLE:OFF
@NotThreadSafe
public class InteractiveChartPanel extends JPanel {
    //CHECKSTYLE:ON

    public static final double MOUSE_SCROLL_FACTOR = 0.05D;
    public static final double HOTKEY_SCROLL_FACTOR = 0.1D;
    public static final int MAX_ZOOM_ITEM_COUNT = 10000;
    private static final int MIN_ZOOM_ITEM_COUNT = 10;
    private static final Duration SCROLL_LOCK_DURATION = new Duration(250, FTimeUnit.MILLISECONDS);

    private final NumberAxis domainAxis;
    private final IndexedDateTimeOHLCDataset dataset;
    private final CustomCombinedDomainXYPlot combinedPlot;
    private XYPlot ohlcPlot;
    private final JFreeChart chart;
    private final CustomChartPanel chartPanel;
    private final IndexedDateTimeNumberFormat domainAxisFormat;
    private final PlotResizeHelper plotResizeHelper = new PlotResizeHelper(this);
    private final PlotCrosshairHelper plotCrosshairHelper = new PlotCrosshairHelper(this);
    private final PlotLegendHelper plotLegendHelper = new PlotLegendHelper(this);
    private final PlotNavigationHelper plotNavigationHelper = new PlotNavigationHelper(this);
    private final PlotConfigurationHelper plotConfigurationHelper = new PlotConfigurationHelper(this);
    private CustomCandlestickRenderer candlestickRenderer;
    private FDate lastHorizontalScroll = FDate.MIN_DATE;
    private FDate lastVerticalScroll = FDate.MIN_DATE;

    public InteractiveChartPanel(final IndexedDateTimeOHLCDataset dataset) {
        this.dataset = dataset;

        domainAxis = new NumberAxis();
        domainAxis.setAutoRange(true);
        domainAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        domainAxisFormat = new IndexedDateTimeNumberFormat(dataset, domainAxis);
        domainAxis.setNumberFormatOverride(domainAxisFormat);

        combinedPlot = new CustomCombinedDomainXYPlot(this);
        combinedPlot.setDataset(dataset);
        combinedPlot.setDomainPannable(true);

        dataset.addChangeListener(new DatasetChangeListenerImpl());
        plotLegendHelper.setDatasetTrashable(dataset, false);

        initPlots();
        chart = new JFreeChart(null, null, combinedPlot, false);
        chartPanel = new CustomChartPanel(chart, true) {
            @Override
            protected boolean isPanAllowed() {
                return !isHighlighting();
            }
        };

        chartPanel.setAllowedRangeGap(2);
        chartPanel.setMouseZoomable(false);
        chartPanel.setRangeZoomable(false);
        chartPanel.setDomainZoomable(true);
        chartPanel.addMouseWheelListener(new MouseWheelListenerImpl());
        chartPanel.addMouseMotionListener(new MouseMotionListenerImpl());
        chartPanel.addKeyListener(new KeyListenerImpl());
        chartPanel.setFocusable(true); //key listener only works on focusable panels

        new JFreeChartLocaleChanger().process(chart);

        chartPanel.setDisplayToolTips(false);
        chartPanel.addChartMouseListener(new ChartMouseListenerImpl());
        chartPanel.addMouseListener(new MouseListenerImpl());
        chartPanel.addMouseWheelListener(new MouseWheelListenerImpl());
        setLayout(new GridLayout());
        add(chartPanel);
        resetRange();
    }

    public PlotCrosshairHelper getPlotCrosshairHelper() {
        return plotCrosshairHelper;
    }

    public PlotResizeHelper getPlotResizeHelper() {
        return plotResizeHelper;
    }

    public PlotLegendHelper getPlotLegendHelper() {
        return plotLegendHelper;
    }

    public PlotNavigationHelper getPlotNavigationHelper() {
        return plotNavigationHelper;
    }

    public PlotConfigurationHelper getPlotConfigurationHelper() {
        return plotConfigurationHelper;
    }

    public CustomCandlestickRenderer getCandlestickRenderer() {
        return candlestickRenderer;
    }

    protected int initRangeAxisDecimalDigits() {
        return 2;
    }

    public int getAllowedRangeGap() {
        return chartPanel.getAllowedRangeGap();
    }

    public IndexedDateTimeOHLCDataset getDataset() {
        return dataset;
    }

    public NumberAxis getDomainAxis() {
        return domainAxis;
    }

    public IndexedDateTimeNumberFormat getDomainAxisFormat() {
        return domainAxisFormat;
    }

    public CustomCombinedDomainXYPlot getCombinedPlot() {
        return combinedPlot;
    }

    public JFreeChart getChart() {
        return chart;
    }

    public CustomChartPanel getChartPanel() {
        return chartPanel;
    }

    public void resetRange() {
        final Range range = new Range(dataset.getItemCount(0) - 200,
                dataset.getItemCount(0) + chartPanel.getAllowedRangeGap());
        domainAxis.setRange(range);
        update();
    }

    protected void initPlots() {
        candlestickRenderer = new CustomCandlestickRenderer(dataset);
        candlestickRenderer.setDrawVolume(false);
        candlestickRenderer.setDataBoundsIncludesVisibleSeriesOnly(true);
        final int precision = initRangeAxisDecimalDigits();
        ohlcPlot = new XYPlot(dataset, domainAxis, XYPlots.newRangeAxis(precision), candlestickRenderer);
        ohlcPlot.setRangeAxisLocation(AxisLocation.BOTTOM_OR_RIGHT);
        plotLegendHelper.addLegendAnnotation(ohlcPlot);
        dataset.setPlot(ohlcPlot);
        dataset.setPrecision(precision);
        //give main plot twice the weight
        combinedPlot.add(ohlcPlot, CustomCombinedDomainXYPlot.MAIN_PLOT_WEIGHT);
        XYPlots.updateRangeAxisPrecision(ohlcPlot);
    }

    public void update() {
        limitRange();
        configureRangeAxis();
        plotCrosshairHelper.disableCrosshair();
        plotLegendHelper.update();
    }

    private void configureRangeAxis() {
        final List<XYPlot> plots = combinedPlot.getSubplots();
        for (final XYPlot plot : plots) {
            plot.getRangeAxis().configure();
        }
    }

    private void limitRange() {
        Range range = domainAxis.getRange();
        boolean rangeChanged = false;
        final double minLowerBound = 0D - chartPanel.getAllowedRangeGap();
        if (range.getLowerBound() < minLowerBound) {
            range = new Range(minLowerBound, range.getUpperBound());
            rangeChanged = true;
        }
        final int maxUpperBound = dataset.getItemCount(0) + chartPanel.getAllowedRangeGap();
        if (range.getUpperBound() > maxUpperBound) {
            range = new Range(range.getLowerBound(), maxUpperBound);
            rangeChanged = true;
        }
        final int itemCount = (int) range.getLength();
        if (itemCount <= MIN_ZOOM_ITEM_COUNT) {
            final int gap = MIN_ZOOM_ITEM_COUNT / 2;
            range = new Range(range.getCentralValue() - gap, range.getCentralValue() + gap);
            if (range.getUpperBound() > maxUpperBound) {
                range = new Range(maxUpperBound - MIN_ZOOM_ITEM_COUNT, maxUpperBound);
            }
            if (range.getLowerBound() < minLowerBound) {
                range = new Range(minLowerBound, MIN_ZOOM_ITEM_COUNT);
            }
            rangeChanged = true;
        }
        if (itemCount >= MAX_ZOOM_ITEM_COUNT) {
            final int gap = MAX_ZOOM_ITEM_COUNT / 2;
            range = new Range(range.getCentralValue() - gap, range.getCentralValue() + gap);
            if (range.getUpperBound() > maxUpperBound) {
                range = new Range(maxUpperBound - MAX_ZOOM_ITEM_COUNT, maxUpperBound);
            }
            if (range.getLowerBound() < minLowerBound) {
                range = new Range(minLowerBound, MAX_ZOOM_ITEM_COUNT);
            }
            rangeChanged = true;
        }
        if (rangeChanged) {
            domainAxis.setRange(range);
        }
    }

    public void zoomOut() {
        chartPanel.zoomOutDomain(-1, -1);
        update();
    }

    public void zoomIn() {
        chartPanel.zoomInDomain(-1, -1);
        update();
    }

    public void panLeft(final double scrollFactor) {
        final Range range = domainAxis.getRange();
        final double length = range.getLength();
        final double newLowerBound = Doubles.max(range.getLowerBound() - length * scrollFactor,
                0 - chartPanel.getAllowedRangeGap());
        final Range newRange = new Range(newLowerBound, newLowerBound + length);
        domainAxis.setRange(newRange);
        update();
    }

    public void panRight(final double scrollFactor) {
        final Range range = domainAxis.getRange();
        final double length = range.getLength();
        final double newUpperBound = Doubles.min(range.getUpperBound() + length * scrollFactor,
                dataset.getItemCount(0) + chartPanel.getAllowedRangeGap());
        final Range newRange = new Range(newUpperBound - length, newUpperBound);
        domainAxis.setRange(newRange);
        update();
    }

    private final class DatasetChangeListenerImpl implements DatasetChangeListener {
        @Override
        public void datasetChanged(final DatasetChangeEvent event) {
            plotCrosshairHelper.datasetChanged();
        }
    }

    private final class MouseListenerImpl extends MouseAdapter {

        @Override
        public void mouseExited(final MouseEvent e) {
            if (plotConfigurationHelper.isShowing()) {
                return;
            }
            InteractiveChartPanel.this.mouseExited();
        }

        @Override
        public void mousePressed(final MouseEvent e) {
            chartPanel.requestFocusInWindow();

            plotConfigurationHelper.mousePressed(e);
            if (plotConfigurationHelper.isShowing()) {
                return;
            }

            plotResizeHelper.mousePressed(e);
            plotLegendHelper.mousePressed(e);
            plotNavigationHelper.mousePressed(e);
            if (new Duration(lastVerticalScroll).isGreaterThan(SCROLL_LOCK_DURATION)) {
                if (e.getButton() == 4) {
                    panLeft(MOUSE_SCROLL_FACTOR);
                    lastHorizontalScroll = new FDate();
                } else if (e.getButton() == 5) {
                    panRight(MOUSE_SCROLL_FACTOR);
                    lastHorizontalScroll = new FDate();
                }
            }
        }

        @Override
        public void mouseReleased(final MouseEvent e) {
            plotConfigurationHelper.mouseReleased(e);
            if (plotConfigurationHelper.isShowing()) {
                return;
            }
            plotLegendHelper.mouseReleased(e);
            plotResizeHelper.mouseReleased(e);
            plotNavigationHelper.mouseReleased(e);
        }
    }

    private final class ChartMouseListenerImpl extends ChartMouseListenerSupport {

        @Override
        public void chartMouseMoved(final ChartMouseEvent event) {
            if (plotConfigurationHelper.isShowing()) {
                //keep the crosshair as it is when making a right click screenshot
                return;
            }
            if (plotLegendHelper.isHighlighting() || plotNavigationHelper.isHighlighting()) {
                plotCrosshairHelper.disableCrosshair();
            } else {
                plotCrosshairHelper.mouseMoved(event);
            }
            plotLegendHelper.mouseMoved(event);
        }

    }

    private final class KeyListenerImpl extends KeyAdapter {
        @Override
        public void keyPressed(final KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_PLUS || e.getKeyCode() == KeyEvent.VK_ADD) {
                zoomIn();
            } else if (e.getKeyCode() == KeyEvent.VK_MINUS || e.getKeyCode() == KeyEvent.VK_SUBTRACT) {
                zoomOut();
            } else if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_KP_RIGHT
                    || e.getKeyCode() == KeyEvent.VK_NUMPAD6) {
                panRight(HOTKEY_SCROLL_FACTOR);
            } else if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_KP_LEFT
                    || e.getKeyCode() == KeyEvent.VK_NUMPAD4) {
                panLeft(HOTKEY_SCROLL_FACTOR);
            }
        }
    }

    private final class MouseMotionListenerImpl extends MouseMotionAdapter {

        @Override
        public void mouseDragged(final MouseEvent e) {
            if (plotConfigurationHelper.isShowing()) {
                return;
            }

            plotResizeHelper.mouseDragged(e);
            plotLegendHelper.mouseDragged(e);
            if (plotLegendHelper.isHighlighting()) {
                plotNavigationHelper.mouseExited();
            } else {
                plotNavigationHelper.mouseDragged(e);
            }
            update();
        }

        @Override
        public void mouseMoved(final MouseEvent e) {
            if (plotConfigurationHelper.isShowing()) {
                return;
            }
            plotResizeHelper.mouseMoved(e);
            plotNavigationHelper.mouseMoved(e);
        }

    }

    private final class MouseWheelListenerImpl implements MouseWheelListener {
        @Override
        public void mouseWheelMoved(final MouseWheelEvent e) {
            if (new Duration(lastHorizontalScroll).isGreaterThan(SCROLL_LOCK_DURATION)) {
                if (e.isShiftDown()) {
                    if (e.getWheelRotation() > 0) {
                        panLeft(MOUSE_SCROLL_FACTOR);
                    } else {
                        panRight(MOUSE_SCROLL_FACTOR);
                    }
                } else {
                    if (e.getWheelRotation() > 0) {
                        zoomOut();
                    } else {
                        zoomIn();
                    }
                }
                lastVerticalScroll = new FDate();
            }
            chartPanel.requestFocusInWindow();
        }
    }

    @Override
    public void setCursor(final Cursor cursor) {
        if (!chartPanel.isPanning() && !isHighlighting()) {
            chartPanel.setCursor(cursor);
        }
    }

    private boolean isHighlighting() {
        return plotLegendHelper.isHighlighting() || plotNavigationHelper.isHighlighting()
                || plotConfigurationHelper.isShowing();
    }

    public void mouseExited() {
        plotCrosshairHelper.disableCrosshair();
        plotLegendHelper.disableHighlighting();
        plotNavigationHelper.mouseExited();
    }

    public XYPlot getOhlcPlot() {
        return ohlcPlot;
    }

    public XYPlot newPlot(final int precision) {
        final NumberAxis rangeAxis = XYPlots.newRangeAxis(precision);
        final XYPlot newPlot = new XYPlot(null, null, rangeAxis, null);
        newPlot.setRangeAxisLocation(AxisLocation.BOTTOM_OR_RIGHT);
        plotLegendHelper.addLegendAnnotation(newPlot);
        return newPlot;
    }

    @Override
    public void updateUI() {
        if (plotConfigurationHelper != null) {
            SwingUtilities.updateComponentTreeUI(plotConfigurationHelper.getPopupMenu());
        }
        super.updateUI();
    }

}
