package de.invesdwin.context.jfreechart.panel.helper;

import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;
import javax.swing.Timer;

import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.XYAnnotationEntity;
import org.jfree.chart.plot.XYPlot;

import de.invesdwin.context.jfreechart.icon.XYIconAnnotation;
import de.invesdwin.context.jfreechart.icon.XYIconAnnotationEntity;
import de.invesdwin.context.jfreechart.panel.InteractiveChartPanel;
import de.invesdwin.context.jfreechart.panel.basis.CustomCombinedDomainXYPlot;
import de.invesdwin.context.jfreechart.panel.helper.icons.PlotIcons;
import de.invesdwin.util.math.Doubles;

@NotThreadSafe
public class PlotNavigationHelper {

    private static final int BUTTON_TIMER_DELAY = 50;
    private static final double BUTTON_SCROLL_FACTOR = 0.05D;
    private static final float INVISIBLE_ALPHA = 0.0f;
    private static final float VISIBLE_ALPHA = 0.6f;
    private static final float HIGHLIGHTED_ALPHA = 1f;

    private final InteractiveChartPanel chartPanel;
    private final XYIconAnnotation panLeft;
    private final XYIconAnnotation zoomOut;
    private final XYIconAnnotation reset;
    private final XYIconAnnotation configure;
    private final XYIconAnnotation zoomIn;
    private final XYIconAnnotation panRight;

    private final XYIconAnnotation panLeft_highlighted;
    private final XYIconAnnotation zoomOut_highlighted;
    private final XYIconAnnotation reset_highlighted;
    private final XYIconAnnotation configure_highlighted;
    private final XYIconAnnotation zoomIn_highlighted;
    private final XYIconAnnotation panRight_highlighted;

    private final XYIconAnnotation panLeft_invisible;
    private final XYIconAnnotation zoomOut_invisible;
    private final XYIconAnnotation reset_invisible;
    private final XYIconAnnotation configure_invisible;
    private final XYIconAnnotation zoomIn_invisible;
    private final XYIconAnnotation panRight_invisible;

    private final List<XYIconAnnotation> annotations = new ArrayList<>();

    private final XYIconAnnotation[] visibleCheckAnnotations;

    private XYPlot shownOnPlot;
    private XYIconAnnotation highlightedAnnotation;
    private Shape highlightingArea;
    private boolean highlighting = false;
    private boolean visible = false;
    private Timer buttonTimer;
    private XYIconAnnotation buttonTimerAnnotation;

    public PlotNavigationHelper(final InteractiveChartPanel chartPanel) {
        this.chartPanel = chartPanel;
        this.panLeft = newIcon(PlotIcons.PAN_LEFT, -60 - 15, VISIBLE_ALPHA);
        this.zoomOut = newIcon(PlotIcons.ZOOM_OUT, -30 - 15, VISIBLE_ALPHA);
        this.reset = newIcon(PlotIcons.RESET, -15, VISIBLE_ALPHA);
        this.configure = newIcon(PlotIcons.CONFIGURE, +15, VISIBLE_ALPHA);
        this.zoomIn = newIcon(PlotIcons.ZOOM_IN, +30 + 15, VISIBLE_ALPHA);
        this.panRight = newIcon(PlotIcons.PAN_RIGHT, +60 + 15, VISIBLE_ALPHA);

        this.panLeft_highlighted = newIcon(PlotIcons.PAN_LEFT, -60 - 15, HIGHLIGHTED_ALPHA);
        this.zoomOut_highlighted = newIcon(PlotIcons.ZOOM_OUT, -30 - 15, HIGHLIGHTED_ALPHA);
        this.reset_highlighted = newIcon(PlotIcons.RESET, -15, HIGHLIGHTED_ALPHA);
        this.configure_highlighted = newIcon(PlotIcons.CONFIGURE, +15, HIGHLIGHTED_ALPHA);
        this.zoomIn_highlighted = newIcon(PlotIcons.ZOOM_IN, +30 + 15, HIGHLIGHTED_ALPHA);
        this.panRight_highlighted = newIcon(PlotIcons.PAN_RIGHT, +60 + 15, HIGHLIGHTED_ALPHA);

        this.panLeft_invisible = newIcon(PlotIcons.PAN_LEFT, -60 - 15, INVISIBLE_ALPHA);
        this.zoomOut_invisible = newIcon(PlotIcons.ZOOM_OUT, -30 - 15, INVISIBLE_ALPHA);
        this.reset_invisible = newIcon(PlotIcons.RESET, -15, INVISIBLE_ALPHA);
        this.configure_invisible = newIcon(PlotIcons.RESET, +15, INVISIBLE_ALPHA);
        this.zoomIn_invisible = newIcon(PlotIcons.ZOOM_IN, +30 + 15, INVISIBLE_ALPHA);
        this.panRight_invisible = newIcon(PlotIcons.PAN_RIGHT, +60 + 15, INVISIBLE_ALPHA);

        this.visibleCheckAnnotations = new XYIconAnnotation[] { panLeft, panLeft_highlighted, panLeft_invisible,
                panRight, panRight_highlighted, panRight_invisible };
    }

    private XYIconAnnotation newIcon(final PlotIcons icon, final int xModification, final float alpha) {
        final XYIconAnnotation annotation = new XYIconAnnotation(0.5D, 0.05D, icon.newIcon(24, alpha)) {
            @Override
            protected double modifyYInput(final double y) {
                return Doubles.min(y * chartPanel.getCombinedPlot().getSubplots().size(), 0.5D);
            }

            @Override
            protected float modifyXOutput(final float x) {
                return x + xModification;
            }

        };
        annotations.add(annotation);
        return annotation;
    }

    public void mouseDragged(final MouseEvent e) {
        mouseMoved(e);
    }

    public void mouseMoved(final MouseEvent e) {
        final int mouseX = e.getX();
        final int mouseY = e.getY();
        final CustomCombinedDomainXYPlot combinedPlot = chartPanel.getCombinedPlot();
        final List<XYPlot> subplots = combinedPlot.getSubplots();
        final int lastSubPlotIndex = subplots.size() - 1;
        final XYPlot lastSubPlot = subplots.get(lastSubPlotIndex);
        if (combinedPlot.getSubplotIndex(mouseX, mouseY) == lastSubPlotIndex) {
            if (lastSubPlot != shownOnPlot) {
                mouseExited();
                shownOnPlot = lastSubPlot;
                addAnnotations(shownOnPlot, false, null);
            }
            final ChartEntity entityForPoint = chartPanel.getChartPanel().getEntityForPoint(mouseX, mouseY);
            XYIconAnnotation newHighlightedAnnotation = null;
            if (entityForPoint instanceof XYIconAnnotationEntity) {
                final XYIconAnnotationEntity l = (XYIconAnnotationEntity) entityForPoint;
                newHighlightedAnnotation = getIconAnnotation(l);
            }
            final boolean newVisible = findVisibleEntity(mouseX, mouseY);
            this.highlighting = determineHighlighting(mouseX, mouseY);
            if (newHighlightedAnnotation != this.highlightedAnnotation || visible != newVisible) {
                removeAnnotations(shownOnPlot, false);
                addAnnotations(shownOnPlot, newVisible, newHighlightedAnnotation);
                highlightingArea = null;
                this.highlightedAnnotation = newHighlightedAnnotation;
                this.visible = newVisible;
                if (buttonTimerAnnotation != null && highlightedAnnotation != buttonTimerAnnotation) {
                    stopButtonTimer();
                }
            }
            if (this.highlighting) {
                chartPanel.getChartPanel().setCursor(PlotResizeHelper.DEFAULT_CURSOR);
            }
        } else {
            mouseExited();
        }
    }

    private boolean findVisibleEntity(final int mouseX, final int mouseY) {
        final Shape area = new Rectangle2D.Double(mouseX - 150, mouseY - 100, 300, 200);
        for (int i = 0; i < visibleCheckAnnotations.length; i++) {
            final XYIconAnnotation annotation = visibleCheckAnnotations[i];
            final XYAnnotationEntity entity = annotation.getEntity();
            if (entity != null) {
                final Rectangle2D entityArea = (Rectangle2D) entity.getArea();
                if (area.intersects(entityArea)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean determineHighlighting(final int mouseX, final int mouseY) {
        final Shape area = getHighlightingArea();
        if (area == null) {
            return false;
        }
        return area.contains(mouseX, mouseY);
    }

    private Shape getHighlightingArea() {
        if (highlightingArea == null) {
            Double minX = null;
            Double minY = null;
            Double maxX = null;
            Double maxY = null;

            for (int i = 0; i < visibleCheckAnnotations.length; i++) {
                final XYIconAnnotation annotation = visibleCheckAnnotations[i];
                final XYAnnotationEntity entity = annotation.getEntity();
                if (entity != null) {
                    final Rectangle2D entityArea = (Rectangle2D) entity.getArea();
                    minX = Doubles.min(minX, entityArea.getX());
                    minY = Doubles.min(minY, entityArea.getY());
                    maxX = Doubles.max(maxX, entityArea.getMaxX());
                    maxY = Doubles.max(maxY, entityArea.getMaxY());
                }
            }
            if (minY == null) {
                return null;
            }
            highlightingArea = new Rectangle2D.Double(minX, minY, maxX - minX, maxY - minY);
        }
        return highlightingArea;
    }

    private XYIconAnnotation getIconAnnotation(final XYIconAnnotationEntity l) {
        if (l == null) {
            return null;
        }
        final XYIconAnnotation highlighted;
        final XYIconAnnotation io = l.getIconAnnotation();
        if (io == panLeft || io == panLeft_highlighted || io == panLeft_invisible) {
            highlighted = panLeft;
        } else if (io == zoomOut || io == zoomOut_highlighted || io == zoomOut_invisible) {
            highlighted = zoomOut;
        } else if (io == reset || io == reset_highlighted || io == reset_invisible) {
            highlighted = reset;
        } else if (io == configure || io == configure_highlighted || io == configure_invisible) {
            highlighted = configure;
        } else if (io == zoomIn || io == zoomIn_highlighted || io == zoomIn_invisible) {
            highlighted = zoomIn;
        } else if (io == panRight || io == panRight_highlighted || io == panRight_invisible) {
            highlighted = panRight;
        } else {
            highlighted = null;
        }
        return highlighted;
    }

    private void removeAnnotations(final XYPlot plot, final boolean notify) {
        final int lastIndex = annotations.size() - 1;
        for (int i = 0; i <= lastIndex; i++) {
            removeAnnotation(plot, annotations.get(i), i == lastIndex);
        }
    }

    private void removeAnnotation(final XYPlot plot, final XYIconAnnotation annotation, final boolean notify) {
        annotation.setEntity(null);
        plot.removeAnnotation(annotation, notify);
    }

    private void addAnnotations(final XYPlot plot, final boolean visible, final XYIconAnnotation highlighted) {
        if (visible) {
            if (highlighted == panLeft) {
                plot.addAnnotation(panLeft_highlighted, false);
            } else {
                plot.addAnnotation(panLeft, false);
            }
            if (highlighted == zoomOut) {
                plot.addAnnotation(zoomOut_highlighted, false);
            } else {
                plot.addAnnotation(zoomOut, false);
            }
            if (highlighted == reset) {
                plot.addAnnotation(reset_highlighted, false);
            } else {
                plot.addAnnotation(reset, false);
            }
            if (highlighted == configure) {
                plot.addAnnotation(configure_highlighted, false);
            } else {
                plot.addAnnotation(configure, false);
            }
            if (highlighted == zoomIn) {
                plot.addAnnotation(zoomIn_highlighted, false);
            } else {
                plot.addAnnotation(zoomIn, false);
            }
            if (highlighted == panRight) {
                plot.addAnnotation(panRight_highlighted, true);
            } else {
                plot.addAnnotation(panRight, true);
            }
        } else {
            plot.addAnnotation(panLeft_invisible, false);
            plot.addAnnotation(zoomOut_invisible, false);
            plot.addAnnotation(reset_invisible, false);
            plot.addAnnotation(configure_invisible, false);
            plot.addAnnotation(zoomIn_invisible, false);
            plot.addAnnotation(panRight_invisible, true);
        }
    }

    public void mouseExited() {
        if (shownOnPlot != null) {
            removeAnnotations(shownOnPlot, true);
            shownOnPlot = null;
        }
        stopButtonTimer();
    }

    public void mousePressed(final MouseEvent e) {
        mouseMoved(e); //update highlighting when popup is dismissed
        if (e.getButton() != MouseEvent.BUTTON1) {
            return;
        }
        final int mouseX = e.getX();
        final int mouseY = e.getY();
        final ChartEntity entityForPoint = chartPanel.getChartPanel().getEntityForPoint(mouseX, mouseY);
        if (entityForPoint instanceof XYIconAnnotationEntity) {
            final XYIconAnnotationEntity l = (XYIconAnnotationEntity) entityForPoint;
            final XYIconAnnotation annotation = getIconAnnotation(l);
            final ActionListener action;
            if (annotation == panLeft) {
                action = new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        chartPanel.panLeft(BUTTON_SCROLL_FACTOR);
                    }
                };
            } else if (annotation == panRight) {
                action = new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        chartPanel.panRight(BUTTON_SCROLL_FACTOR);
                    }
                };
            } else if (annotation == zoomIn) {
                action = new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        chartPanel.zoomIn();
                    }
                };
            } else if (annotation == zoomOut) {
                action = new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        chartPanel.zoomOut();
                    }
                };
            } else {
                action = null;
            }
            if (action != null) {
                startButtonTimer(annotation, action);
            }
        }
    }

    private void startButtonTimer(final XYIconAnnotation annotation, final ActionListener action) {
        buttonTimer = new Timer(BUTTON_TIMER_DELAY, action);
        buttonTimer.setInitialDelay(0);
        buttonTimer.start();
        buttonTimerAnnotation = annotation;
    }

    public void mouseReleased(final MouseEvent e) {
        if (e.getButton() != MouseEvent.BUTTON1) {
            return;
        }
        if (stopButtonTimer()) {
            return;
        }
        final int mouseX = e.getX();
        final int mouseY = e.getY();
        final ChartEntity entityForPoint = chartPanel.getChartPanel().getEntityForPoint(mouseX, mouseY);
        if (entityForPoint instanceof XYIconAnnotationEntity) {
            final XYIconAnnotationEntity l = (XYIconAnnotationEntity) entityForPoint;
            final XYIconAnnotation iconAnnotation = getIconAnnotation(l);
            if (iconAnnotation == reset) {
                chartPanel.resetRange();
            } else if (iconAnnotation == configure) {
                chartPanel.getPlotConfigurationHelper().displayPopupMenu(mouseX, mouseY);
            }
        }

    }

    private boolean stopButtonTimer() {
        if (buttonTimer != null) {
            buttonTimer.stop();
            buttonTimer = null;
            buttonTimerAnnotation = null;
            return true;
        }
        return false;
    }

    public boolean isHighlighting() {
        return highlighting;
    }

}
