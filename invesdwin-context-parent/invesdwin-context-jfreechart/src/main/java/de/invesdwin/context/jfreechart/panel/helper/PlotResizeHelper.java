package de.invesdwin.context.jfreechart.panel.helper;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import org.jfree.chart.plot.XYPlot;

import de.invesdwin.aspects.EventDispatchThreadUtil;
import de.invesdwin.context.jfreechart.panel.InteractiveChartPanel;
import de.invesdwin.context.jfreechart.panel.basis.CustomCombinedDomainXYPlot;
import de.invesdwin.util.math.Doubles;

@NotThreadSafe
public class PlotResizeHelper {

    public static final Cursor RESIZE_CURSOR = Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR);
    public static final Cursor DEFAULT_CURSOR = Cursor.getDefaultCursor();
    private static final int INITIAL_PLOT_WEIGHT = CustomCombinedDomainXYPlot.INITIAL_PLOT_WEIGHT;
    private static final int RESIZE_WEIGHT_MIN_MODIFICATION = 10;

    private final InteractiveChartPanel chartPanel;
    private Integer plotResizeAboveIndex;
    private XYPlot plotResizeAbove;
    private Integer plotResizeBelowIndex;
    private XYPlot plotResizeBelow;
    private Point plotResizePointStart;
    private Point plotResizePointEnd;
    private boolean plotResizeActive = false;

    public PlotResizeHelper(final InteractiveChartPanel chartPanel) {
        this.chartPanel = chartPanel;
    }

    private boolean startResize(final int mouseX, final int mouseY) {
        boolean found = false;
        final int subplotIndex = chartPanel.getCombinedPlot().getSubplotIndex(mouseX, mouseY);
        if (subplotIndex == -1) {
            final int subplotIndexAbove = chartPanel.getCombinedPlot().getSubplotIndex(mouseX, mouseY - 20);
            if (subplotIndexAbove != -1) {
                final int subplotIndexBelow = chartPanel.getCombinedPlot().getSubplotIndex(mouseX, mouseY + 20);
                if (subplotIndexBelow != -1) {
                    final List<XYPlot> subplotsList = chartPanel.getCombinedPlot().getSubplots();
                    plotResizeAboveIndex = subplotIndexAbove;
                    plotResizeAbove = subplotsList.get(subplotIndexAbove);
                    plotResizeBelowIndex = subplotIndexBelow;
                    plotResizeBelow = subplotsList.get(subplotIndexBelow);
                    plotResizePointStart = new Point(mouseX, mouseY);
                    found = true;
                }
            }
        }
        if (!found) {
            cleanup();
        }
        return found;
    }

    private void cleanup() {
        plotResizeAboveIndex = null;
        plotResizeAbove = null;
        plotResizeBelowIndex = null;
        plotResizeBelow = null;
        plotResizePointStart = null;
        plotResizePointEnd = null;
        plotResizeActive = false;
        chartPanel.setCursor(DEFAULT_CURSOR);
    }

    private void initResizeCursor(final int mouseX, final int mouseY) {
        final int subplotIndex = chartPanel.getCombinedPlot().getSubplotIndex(mouseX, mouseY);
        if (subplotIndex == -1) {
            boolean found = false;
            final int subplotIndexAbove = chartPanel.getCombinedPlot().getSubplotIndex(mouseX, mouseY - 20);
            if (subplotIndexAbove != -1) {
                final int subplotIndexBelow = chartPanel.getCombinedPlot().getSubplotIndex(mouseX, mouseY + 20);
                if (subplotIndexBelow != -1) {
                    chartPanel.setCursor(RESIZE_CURSOR);
                    found = true;
                }
            }
            if (!found) {
                chartPanel.setCursor(DEFAULT_CURSOR);
            }
        }
    }

    private void resize() {
        if (plotResizePointStart != null && plotResizePointEnd != null) {
            boolean resized = false;
            final int startMinusEnd = plotResizePointStart.y - plotResizePointEnd.y;
            final int resizeWeight = (int) Doubles.min(100, Doubles.max(RESIZE_WEIGHT_MIN_MODIFICATION,
                    RESIZE_WEIGHT_MIN_MODIFICATION * (Doubles.abs(startMinusEnd) / 10)));

            if (startMinusEnd > 0 && chartPanel.getCombinedPlot()
                    .getSubplotIndex(plotResizePointEnd.x, plotResizePointEnd.y) == plotResizeAboveIndex) {
                //move up
                final int newLowerWeight = plotResizeAbove.getWeight() - resizeWeight;
                if (newLowerWeight > getMinWeight()) {
                    plotResizeAbove.setWeight(newLowerWeight);
                    plotResizeBelow.setWeight(plotResizeBelow.getWeight() + resizeWeight);
                    resized = true;
                }
            } else if (startMinusEnd < 0 && chartPanel.getCombinedPlot()
                    .getSubplotIndex(plotResizePointEnd.x, plotResizePointEnd.y) == plotResizeBelowIndex) {
                //move down
                final int newLowerWeight = plotResizeBelow.getWeight() - resizeWeight;
                if (newLowerWeight > getMinWeight()) {
                    plotResizeAbove.setWeight(plotResizeAbove.getWeight() + resizeWeight);
                    plotResizeBelow.setWeight(newLowerWeight);
                    resized = true;
                }
            }
            if (resized) {
                chartPanel.setCursor(RESIZE_CURSOR);
                EventDispatchThreadUtil.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        continueResize(startMinusEnd);
                    }
                });
            } else {
                //mouse moved outside but might move inside again, thus don't modify start point
                plotResizeActive = false;
            }
        } else {
            plotResizeActive = false;
        }
    }

    private void continueResize(final int startMinusEnd) {
        if (plotResizePointEnd != null && plotResizePointEnd.y >= 0) {
            final int subplotIndex = chartPanel.getCombinedPlot()
                    .getSubplotIndex(plotResizePointEnd.x, plotResizePointEnd.y);
            if (subplotIndex != -1) {
                if (startMinusEnd > 0) {
                    //was up move
                    for (int y = plotResizePointStart.y; y >= plotResizePointEnd.y; y--) {
                        if (chartPanel.getCombinedPlot().getSubplotIndex(plotResizePointEnd.x, y) == -1) {
                            plotResizePointStart = new Point(plotResizePointStart.x, y);
                        }
                    }
                } else if (startMinusEnd < 0) {
                    //was down move
                    for (int y = plotResizePointStart.y; y <= plotResizePointEnd.y; y++) {
                        if (chartPanel.getCombinedPlot().getSubplotIndex(plotResizePointEnd.x, y) == -1) {
                            plotResizePointStart = new Point(plotResizePointStart.x, y);
                        }
                    }
                }
                resize();
            } else {
                maybeUpdatePlotResizeStart(subplotIndex);
                plotResizeActive = false;
            }
        } else {
            plotResizeActive = false;
        }
    }

    private void maybeUpdatePlotResizeStart(final int subplotIndex) {
        if (subplotIndex == -1) {
            final int subplotIndexAbove = chartPanel.getCombinedPlot()
                    .getSubplotIndex(plotResizePointEnd.x, plotResizePointEnd.y - 20);
            if (subplotIndexAbove != -1) {
                final int subplotIndexBelow = chartPanel.getCombinedPlot()
                        .getSubplotIndex(plotResizePointEnd.x, plotResizePointEnd.y + 20);
                if (subplotIndexBelow != -1) {
                    plotResizePointStart = plotResizePointEnd;
                }
            }
        }
    }

    private int getMinWeight() {
        return (int) (chartPanel.getCombinedPlot().getSubplots().size() * INITIAL_PLOT_WEIGHT * 0.1);
    }

    public void mouseDragged(final MouseEvent e) {
        plotResizePointEnd = new Point(e.getX(), e.getY());
        if (plotResizePointStart != null) {
            if (!plotResizeActive) {
                plotResizeActive = true;
                resize();
            }
        }
    }

    public void mouseMoved(final MouseEvent e) {
        initResizeCursor(e.getX(), e.getY());
    }

    public void mousePressed(final MouseEvent e) {
        if (e.getButton() != MouseEvent.BUTTON1) {
            return;
        }
        startResize(e.getX(), e.getY());
    }

    public void mouseReleased(final MouseEvent e) {
        if (e.getButton() != MouseEvent.BUTTON1) {
            return;
        }
        cleanup();
    }

}
