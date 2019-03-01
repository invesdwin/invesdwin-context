package de.invesdwin.context.jfreechart.panel;

import java.awt.Cursor;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.Range;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.general.DatasetChangeListener;

import de.invesdwin.context.jfreechart.panel.basis.CustomChartPanel;
import de.invesdwin.context.jfreechart.panel.basis.CustomCombinedDomainXYPlot;
import de.invesdwin.context.jfreechart.panel.helper.PlotCrosshairHelper;
import de.invesdwin.context.jfreechart.panel.helper.PlotNavigationHelper;
import de.invesdwin.context.jfreechart.panel.helper.PlotPanHelper;
import de.invesdwin.context.jfreechart.panel.helper.PlotResizeHelper;
import de.invesdwin.context.jfreechart.panel.helper.PlotZoomHelper;
import de.invesdwin.context.jfreechart.panel.helper.config.PlotConfigurationHelper;
import de.invesdwin.context.jfreechart.panel.helper.legend.PlotLegendHelper;
import de.invesdwin.context.jfreechart.plot.IndexedDateTimeNumberFormat;
import de.invesdwin.context.jfreechart.plot.XYPlots;
import de.invesdwin.context.jfreechart.plot.dataset.IndexedDateTimeOHLCDataset;
import de.invesdwin.context.jfreechart.visitor.JFreeChartLocaleChanger;
import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.swing.listener.KeyListenerSupport;
import de.invesdwin.util.swing.listener.MouseListenerSupport;
import de.invesdwin.util.swing.listener.MouseMotionListenerSupport;
import de.invesdwin.util.swing.listener.MouseWheelListenerSupport;
import de.invesdwin.util.time.duration.Duration;
import de.invesdwin.util.time.fdate.FDate;
import de.invesdwin.util.time.fdate.FTimeUnit;

// CHECKSTYLE:OFF
@NotThreadSafe
public class InteractiveChartPanel extends JPanel {
    //CHECKSTYLE:ON

    private static final Duration SCROLL_LOCK_DURATION = new Duration(250, FTimeUnit.MILLISECONDS);

    private final NumberAxis domainAxis;
    private final IndexedDateTimeOHLCDataset dataset;
    private final CustomCombinedDomainXYPlot combinedPlot;
    private XYPlot ohlcPlot;
    private final JFreeChart chart;
    private final CustomChartPanel chartPanel;
    private final IndexedDateTimeNumberFormat domainAxisFormat;
    private final PlotResizeHelper plotResizeHelper;
    private final PlotCrosshairHelper plotCrosshairHelper;
    private final PlotLegendHelper plotLegendHelper;
    private final PlotNavigationHelper plotNavigationHelper;
    private final PlotConfigurationHelper plotConfigurationHelper;
    private final PlotZoomHelper plotZoomHelper;
    private final PlotPanHelper plotPanHelper;
    private FDate lastHorizontalScroll = FDate.MIN_DATE;
    private FDate lastVerticalScroll = FDate.MIN_DATE;

    public InteractiveChartPanel(final IndexedDateTimeOHLCDataset dataset) {
        this.dataset = dataset;

        this.plotResizeHelper = new PlotResizeHelper(this);
        this.plotCrosshairHelper = new PlotCrosshairHelper(this);
        this.plotLegendHelper = new PlotLegendHelper(this);
        this.plotNavigationHelper = new PlotNavigationHelper(this);
        this.plotConfigurationHelper = new PlotConfigurationHelper(this);
        this.plotZoomHelper = new PlotZoomHelper(this);
        this.plotPanHelper = new PlotPanHelper(this);

        domainAxis = new NumberAxis();
        domainAxis.setAutoRange(true);
        domainAxis.setLabelFont(XYPlots.AXIS_LABEL_FONT);
        domainAxis.setTickLabelFont(XYPlots.AXIS_LABEL_FONT);
        domainAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        domainAxisFormat = new IndexedDateTimeNumberFormat(dataset, domainAxis);
        domainAxis.setNumberFormatOverride(domainAxisFormat);

        combinedPlot = new CustomCombinedDomainXYPlot(this);
        combinedPlot.setDataset(dataset);
        combinedPlot.setDomainPannable(true);

        dataset.addChangeListener(new DatasetChangeListenerImpl());
        plotLegendHelper.setDatasetRemovable(dataset, false);

        initPlots();
        chart = new JFreeChart(null, null, combinedPlot, false);
        chartPanel = new CustomChartPanel(chart, true) {
            @Override
            protected boolean isPanAllowed() {
                return !isHighlighting();
            }
        };

        chartPanel.setAllowedRangeGap(2);
        chartPanel.addMouseWheelListener(new MouseWheelListenerImpl());
        chartPanel.addMouseMotionListener(new MouseMotionListenerImpl());
        chartPanel.addKeyListener(new KeyListenerImpl());
        chartPanel.setFocusable(true); //key listener only works on focusable panels

        new JFreeChartLocaleChanger().process(chart);

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

    public PlotZoomHelper getPlotZoomHelper() {
        return plotZoomHelper;
    }

    public PlotPanHelper getPlotPanHelper() {
        return plotPanHelper;
    }

    protected int initRangeAxisDecimalDigits() {
        return 2;
    }

    protected String initRangeAxisId() {
        return "";
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
        final int minLowerBound = -chartPanel.getAllowedRangeGap();
        final int lowerBound = dataset.getItemCount(0) - getInitialVisibleItemCount();
        final int upperBound = dataset.getItemCount(0) + chartPanel.getAllowedRangeGap();
        final Range range = new Range(Doubles.max(minLowerBound, lowerBound), upperBound);
        domainAxis.setRange(range);
        update();
    }

    public int getInitialVisibleItemCount() {
        return 200;
    }

    protected void initPlots() {
        ohlcPlot = new XYPlot(dataset, domainAxis, XYPlots.newRangeAxis(0, false, false),
                plotConfigurationHelper.getPriceInitialSettings().getPriceRenderer());
        ohlcPlot.setRangeAxisLocation(AxisLocation.BOTTOM_OR_RIGHT);
        plotLegendHelper.addLegendAnnotation(ohlcPlot);
        dataset.setPlot(ohlcPlot);
        dataset.setPrecision(initRangeAxisDecimalDigits());
        dataset.setRangeAxisId(initRangeAxisId());
        //give main plot twice the weight
        combinedPlot.add(ohlcPlot, CustomCombinedDomainXYPlot.MAIN_PLOT_WEIGHT);
        XYPlots.updateRangeAxes(ohlcPlot);
    }

    public void update() {
        plotZoomHelper.limitRange();
        configureRangeAxis();
        plotCrosshairHelper.disableCrosshair();
        plotLegendHelper.update();
    }

    private void configureRangeAxis() {
        final List<XYPlot> plots = combinedPlot.getSubplots();
        for (final XYPlot plot : plots) {
            XYPlots.configureRangeAxes(plot);
        }
    }

    private final class DatasetChangeListenerImpl implements DatasetChangeListener {
        @Override
        public void datasetChanged(final DatasetChangeEvent event) {
            plotCrosshairHelper.datasetChanged();
        }
    }

    private final class MouseListenerImpl extends MouseListenerSupport {

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
                    plotPanHelper.panLeft();
                    lastHorizontalScroll = new FDate();
                } else if (e.getButton() == 5) {
                    plotPanHelper.panRight();
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

    private final class KeyListenerImpl extends KeyListenerSupport {
        @Override
        public void keyPressed(final KeyEvent e) {
            plotPanHelper.keyPressed(e);
            if (e.getKeyCode() == KeyEvent.VK_PLUS || e.getKeyCode() == KeyEvent.VK_ADD) {
                plotZoomHelper.zoomIn();
            } else if (e.getKeyCode() == KeyEvent.VK_MINUS || e.getKeyCode() == KeyEvent.VK_SUBTRACT) {
                plotZoomHelper.zoomOut();
            } else if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_KP_RIGHT
                    || e.getKeyCode() == KeyEvent.VK_NUMPAD6) {
                plotPanHelper.panRight();
            } else if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_KP_LEFT
                    || e.getKeyCode() == KeyEvent.VK_NUMPAD4) {
                plotPanHelper.panLeft();
            }
        }

        @Override
        public void keyReleased(final KeyEvent e) {
            plotPanHelper.keyReleased(e);
        }

    }

    private final class MouseMotionListenerImpl extends MouseMotionListenerSupport {

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
                //keep the crosshair as it is when making a right click screenshot
                return;
            }
            if (plotLegendHelper.isHighlighting() || plotNavigationHelper.isHighlighting()) {
                plotCrosshairHelper.disableCrosshair();
            } else {
                plotCrosshairHelper.mouseMoved(e);
            }
            plotLegendHelper.mouseMoved(e);
            plotResizeHelper.mouseMoved(e);
            plotNavigationHelper.mouseMoved(e);
        }

    }

    private final class MouseWheelListenerImpl extends MouseWheelListenerSupport {
        @Override
        public void mouseWheelMoved(final MouseWheelEvent e) {
            if (new Duration(lastHorizontalScroll).isGreaterThan(SCROLL_LOCK_DURATION)) {
                if (e.isShiftDown()) {
                    if (e.getWheelRotation() > 0) {
                        plotPanHelper.panLeft();
                    } else {
                        plotPanHelper.panRight();
                    }
                } else {
                    plotZoomHelper.mouseWheelMoved(e);
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

    public XYPlot newPlot() {
        final NumberAxis rangeAxis = XYPlots.newRangeAxis(0, false, true);
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

    @Override
    public synchronized void addKeyListener(final KeyListener l) {
        chartPanel.addKeyListener(l);
    }

}
