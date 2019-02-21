package de.invesdwin.context.jfreechart.panel.helper.config;

import java.awt.Color;
import java.awt.Component;
import java.awt.Insets;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.concurrent.NotThreadSafe;
import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.event.PopupMenuEvent;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.jfree.chart.ChartUtils;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYStepRenderer;

import de.invesdwin.context.jfreechart.panel.InteractiveChartPanel;
import de.invesdwin.context.jfreechart.panel.basis.CustomChartTransferable;
import de.invesdwin.context.jfreechart.panel.helper.legend.HighlightedLegendInfo;
import de.invesdwin.context.jfreechart.renderer.CustomOhlcBarRenderer;
import de.invesdwin.context.jfreechart.renderer.CustomOhlcCandlestickRenderer;
import de.invesdwin.context.jfreechart.renderer.CustomVolumeBarRenderer;
import de.invesdwin.context.jfreechart.renderer.CustomXYAreaRenderer;
import de.invesdwin.context.jfreechart.renderer.IUpDownColorRenderer;
import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.error.UnknownArgumentException;
import de.invesdwin.util.lang.Colors;
import de.invesdwin.util.lang.Strings;
import de.invesdwin.util.math.decimal.scaled.Percent;
import de.invesdwin.util.math.decimal.scaled.PercentScale;
import de.invesdwin.util.swing.Dialogs;
import de.invesdwin.util.swing.listener.IColorChooserListener;
import de.invesdwin.util.swing.listener.PopupMenuListenerSupport;

@NotThreadSafe
public class PlotConfigurationHelper {

    public static final Color DEFAULT_DOWN_COLOR = Colors.fromHex("#EF5350");
    public static final Color DEFAULT_UP_COLOR = Colors.fromHex("#26A69A");
    public static final Color DEFAULT_PRICE_COLOR = Colors.fromHex("#3C78D8");
    public static final Stroke DEFAULT_PRICE_STROKE = LineStyleType.Solid.getStroke(LineWidthType._1);
    public static final Percent VOLUME_TRANSPARENCY = new Percent(50D, PercentScale.PERCENT);
    public static final PriceRendererType DEFAULT_PRICE_RENDERER_TYPE = PriceRendererType.Candlestick;

    private final InteractiveChartPanel chartPanel;

    /*
     * the renderers can diverge from these settings using the context menu configuration, though a reset will use these
     * values here again. using the setters will override the context menu configuration
     */
    private PriceRendererType priceRendererType = DEFAULT_PRICE_RENDERER_TYPE;
    private Color upColor;
    private Color downColor;
    private Color priceColor;
    private Stroke priceStroke;

    private final CustomOhlcCandlestickRenderer candlestickRenderer;
    private final CustomOhlcBarRenderer barsRenderer;
    private final CustomVolumeBarRenderer volumeRenderer;
    private final CustomXYAreaRenderer areaRenderer;
    private final StandardXYItemRenderer lineRenderer;
    private final XYStepRenderer stepLineRenderer;

    private final Set<String> volumeSeriesKeys = new HashSet<>();
    private final Map<String, InitialSeriesSettings> seriesKey_initialSeriesSettings = new HashMap<>();

    private JPopupMenu popupMenu;
    private JMenuItem titleItem;
    private JMenu priceRendererItem;
    private JMenu seriesRendererItem;
    private JMenuItem volumeRendererItem;
    private JMenu lineStyleItem;
    private JMenu lineWidthItem;
    private JMenuItem upColorItem;
    private JMenuItem downColorItem;
    private JMenuItem colorItem;
    private JMenuItem copyToClipboardItem;
    private JMenuItem saveAsPNGItem;
    private JMenuItem helpItem;
    private HighlightedLegendInfo highlighted;

    private JMenuItem showSeriesItem;

    private JMenuItem hideSeriesItem;
    private JMenuItem resetSeriesItem;

    public PlotConfigurationHelper(final InteractiveChartPanel chartPanel) {
        this.chartPanel = chartPanel;
        initPopupMenu();

        this.candlestickRenderer = new CustomOhlcCandlestickRenderer(chartPanel.getDataset());
        this.barsRenderer = new CustomOhlcBarRenderer(candlestickRenderer);
        this.volumeRenderer = new CustomVolumeBarRenderer(candlestickRenderer);
        this.areaRenderer = new CustomXYAreaRenderer();

        this.lineRenderer = new StandardXYItemRenderer();
        this.stepLineRenderer = new XYStepRenderer();

        setUpColor(DEFAULT_UP_COLOR);
        setDownColor(DEFAULT_DOWN_COLOR);
        setPriceColor(DEFAULT_PRICE_COLOR);
        setPriceStroke(DEFAULT_PRICE_STROKE);
    }

    public JPopupMenu getPopupMenu() {
        return this.popupMenu;
    }

    public PriceRendererType getPriceRendererType() {
        return priceRendererType;
    }

    public void setPriceRendererType(final PriceRendererType priceRendererType) {
        if (priceRendererType != this.priceRendererType) {
            updatePriceRendererType(priceRendererType);
            chartPanel.update();
        }
        this.priceRendererType = priceRendererType;
    }

    private void updatePriceRendererType(final PriceRendererType priceRendererType) {
        final XYItemRenderer renderer = chartPanel.getOhlcPlot().getRenderer(0);
        final XYItemRenderer newRenderer = getPriceRenderer(priceRendererType);
        newRenderer.setSeriesPaint(0, renderer.getSeriesPaint(0));
        newRenderer.setSeriesStroke(0, renderer.getSeriesStroke(0));
        chartPanel.getOhlcPlot().setRenderer(0, newRenderer);
    }

    public XYItemRenderer getPriceRenderer() {
        return getPriceRenderer(priceRendererType);
    }

    public XYItemRenderer getPriceRenderer(final PriceRendererType priceRendererType) {
        switch (priceRendererType) {
        case Area:
            return areaRenderer;
        case Line:
            return lineRenderer;
        case Step:
            return stepLineRenderer;
        case OHLC:
            return barsRenderer;
        case Candlestick:
            return candlestickRenderer;
        default:
            throw UnknownArgumentException.newInstance(PriceRendererType.class, priceRendererType);
        }
    }

    public PriceRendererType getPriceRendererType(final XYItemRenderer renderer) {
        if (renderer == areaRenderer) {
            return PriceRendererType.Area;
        } else if (renderer == lineRenderer) {
            return PriceRendererType.Line;
        } else if (renderer == stepLineRenderer) {
            return PriceRendererType.Step;
        } else if (renderer == barsRenderer) {
            return PriceRendererType.OHLC;
        } else if (renderer == candlestickRenderer) {
            return PriceRendererType.Candlestick;
        } else {
            throw UnknownArgumentException.newInstance(XYItemRenderer.class, renderer);
        }
    }

    public CustomVolumeBarRenderer getVolumeRenderer() {
        return volumeRenderer;
    }

    public Color getUpColor() {
        return upColor;
    }

    public void setUpColor(final Color upColor) {
        this.upColor = upColor;

        updateUpColorPrice();
        updateUpColorVolume();
    }

    private void updateUpColorPrice() {
        candlestickRenderer.setUpColor(upColor);
    }

    private void updateUpColorVolume() {
        volumeRenderer.setUpColor(Colors.setTransparency(upColor, VOLUME_TRANSPARENCY));
    }

    public Color getDownColor() {
        return downColor;
    }

    public void setDownColor(final Color downColor) {
        this.downColor = downColor;

        updateDownColorPrice();
        updateDownColorVolume();
    }

    private void updateDownColorVolume() {
        volumeRenderer.setDownColor(Colors.setTransparency(downColor, VOLUME_TRANSPARENCY));
    }

    private void updateDownColorPrice() {
        candlestickRenderer.setDownColor(downColor);
    }

    public Color getPriceColor() {
        return priceColor;
    }

    public void setPriceColor(final Color priceColor) {
        this.priceColor = priceColor;

        updatePriceColorPrice();
        updatePriceColorVolume();
    }

    private void updatePriceColorVolume() {
        this.volumeRenderer.setSeriesPaint(0, priceColor);
    }

    private void updatePriceColorPrice() {
        this.candlestickRenderer.setSeriesPaint(0, priceColor);
        this.barsRenderer.setSeriesPaint(0, priceColor);
        this.lineRenderer.setSeriesPaint(0, priceColor);
        this.areaRenderer.setSeriesPaint(0, priceColor);
        this.stepLineRenderer.setSeriesPaint(0, priceColor);
    }

    public void setPriceStroke(final LineStyleType lineStyleType, final LineWidthType lineWidthType) {
        this.priceStroke = lineStyleType.getStroke(lineWidthType);

        updatePriceStrokePrice();
        updatePriceStrokeVolume();
    }

    private void updatePriceStrokeVolume() {
        this.volumeRenderer.setSeriesStroke(0, priceStroke);
    }

    private void updatePriceStrokePrice() {
        this.candlestickRenderer.setSeriesStroke(0, priceStroke);
        this.barsRenderer.setSeriesStroke(0, priceStroke);
        this.lineRenderer.setSeriesStroke(0, priceStroke);
        this.areaRenderer.setSeriesStroke(0, priceStroke);
        this.stepLineRenderer.setSeriesStroke(0, priceStroke);
    }

    public void setPriceStroke(final Stroke priceStroke) {
        Assertions.checkNotNull(LineStyleType.valueOf(priceStroke));
        this.priceStroke = priceStroke;
    }

    public Stroke getPriceStroke() {
        return priceStroke;
    }

    private void initPopupMenu() {

        titleItem = new JMenuItem("");
        titleItem.setEnabled(false);

        initRendererItems();
        initStrokeItems();
        initColorItems();
        initResetItem();
        initShowHideSeriesItems();
        initExportItems();
        initHelpItem();

        popupMenu = new JPopupMenu();
        popupMenu.addPopupMenuListener(new PopupMenuListenerSupport() {

            @Override
            public void popupMenuWillBecomeVisible(final PopupMenuEvent e) {
                popupMenu.removeAll();
                highlighted = chartPanel.getPlotLegendHelper().getHighlightedLegendInfo();
                if (highlighted != null) {
                    if (highlighted.isHidden()) {
                        popupMenu.add(titleItem);
                        popupMenu.add(showSeriesItem);
                    } else {
                        addSeriesConfigMenuItems();
                    }
                } else {
                    popupMenu.add(copyToClipboardItem);
                    popupMenu.add(saveAsPNGItem);
                    popupMenu.add(helpItem);
                }

                chartPanel.getPlotNavigationHelper().mouseExited();
            }

            private void addSeriesConfigMenuItems() {
                if (highlighted.isPriceSeries()) {
                    titleItem.setText(String.valueOf(chartPanel.getDataset().getSeriesKey(0)));
                    priceRendererItem.setVisible(true);
                    seriesRendererItem.setVisible(false);
                } else {
                    final boolean volumeSeries = isVolumeSeries(highlighted);
                    if (!volumeSeries && !seriesKey_initialSeriesSettings.containsKey(highlighted.getSeriesKey())) {
                        seriesKey_initialSeriesSettings.put(highlighted.getSeriesKey(),
                                new InitialSeriesSettings(highlighted.getRenderer()));
                    }
                    volumeRendererItem.setVisible(volumeSeries);
                    titleItem.setText(highlighted.getSeriesKey());
                    priceRendererItem.setVisible(false);
                    seriesRendererItem.setVisible(true);
                }
                updateRendererVisibility();
                updateLineMenuItemVisibility();
                popupMenu.add(titleItem);
                popupMenu.addSeparator();
                popupMenu.add(priceRendererItem);
                popupMenu.add(seriesRendererItem);
                popupMenu.add(lineStyleItem);
                popupMenu.add(lineWidthItem);
                popupMenu.add(upColorItem);
                popupMenu.add(downColorItem);
                popupMenu.add(colorItem);
                popupMenu.add(resetSeriesItem);
                popupMenu.addSeparator();
                popupMenu.add(hideSeriesItem);
            }

            @Override
            public void popupMenuWillBecomeInvisible(final PopupMenuEvent e) {
                chartPanel.getPlotLegendHelper().disableHighlighting();
            }

            @Override
            public void popupMenuCanceled(final PopupMenuEvent e) {
                highlighted = null;
                //only the first popup should have the crosshair visible
                chartPanel.mouseExited();
            }
        });

    }

    private void initResetItem() {
        resetSeriesItem = new JMenuItem("Reset Style");
        resetSeriesItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (highlighted.isPriceSeries()) {
                    updateUpColorPrice();
                    updateDownColorPrice();
                    updatePriceColorPrice();
                    updatePriceStrokePrice();
                    chartPanel.getOhlcPlot().setRenderer(0, getPriceRenderer(priceRendererType));
                } else if (isVolumeSeries(highlighted)) {
                    updateDownColorVolume();
                    updateUpColorVolume();
                    updatePriceColorVolume();
                    updatePriceStrokeVolume();
                    highlighted.setRenderer(volumeRenderer);
                } else {
                    final InitialSeriesSettings initialSeriesSettings = seriesKey_initialSeriesSettings
                            .get(highlighted.getSeriesKey());
                    initialSeriesSettings.reset(highlighted);
                }
            }
        });
    }

    private void updateRendererVisibility() {
        if (priceRendererItem.isVisible()) {
            final PriceRendererType rendererType = getPriceRendererType(chartPanel.getOhlcPlot().getRenderer(0));
            for (final Component component : priceRendererItem.getMenuComponents()) {
                final JRadioButtonMenuItem menuItem = (JRadioButtonMenuItem) component;
                if (menuItem.getName().equals(rendererType.name())) {
                    menuItem.setSelected(true);
                }
            }
            lineStyleItem.setVisible(rendererType.isLineStyleConfigurable());
            lineWidthItem.setVisible(rendererType.isLineWidthConfigurable());
            upColorItem.setVisible(rendererType.isUpColorConfigurable());
            downColorItem.setVisible(rendererType.isDownColorConfigurable());
            colorItem.setVisible(rendererType.isColorConfigurable());
        }
        if (seriesRendererItem.isVisible()) {
            final XYItemRenderer renderer = highlighted.getRenderer();
            if (renderer == getVolumeRenderer()) {
                volumeRendererItem.setSelected(true);
                lineStyleItem.setVisible(false);
                lineWidthItem.setVisible(false);
                upColorItem.setVisible(true);
                downColorItem.setVisible(true);
                colorItem.setVisible(false);
            } else {
                final SeriesRendererType seriesRendererType = SeriesRendererType.valueOf(renderer);
                for (final Component component : seriesRendererItem.getMenuComponents()) {
                    final JRadioButtonMenuItem menuItem = (JRadioButtonMenuItem) component;
                    if (menuItem.getName().equals(seriesRendererType.name())) {
                        menuItem.setSelected(true);
                    }
                }
                lineStyleItem.setVisible(seriesRendererType.isLineStyleConfigurable());
                lineWidthItem.setVisible(seriesRendererType.isLineWidthConfigurable());
                upColorItem.setVisible(seriesRendererType.isUpColorConfigurable());
                downColorItem.setVisible(seriesRendererType.isDownColorConfigurable());
                colorItem.setVisible(seriesRendererType.isColorConfigurable());
            }
        }
    }

    private void updateLineMenuItemVisibility() {
        if (lineStyleItem.isVisible()) {
            final XYItemRenderer renderer = highlighted.getRenderer();
            final LineStyleType lineStyleType = LineStyleType.valueOf(renderer.getSeriesStroke(0));
            for (final Component component : lineStyleItem.getMenuComponents()) {
                final JRadioButtonMenuItem menuItem = (JRadioButtonMenuItem) component;
                if (menuItem.getName().equals(lineStyleType.name())) {
                    menuItem.setSelected(true);
                }
            }
        }
        if (lineWidthItem.isVisible()) {
            final XYItemRenderer renderer = highlighted.getRenderer();
            final LineWidthType lineWidthType = LineWidthType.valueOf(renderer.getSeriesStroke(0));
            for (final Component component : lineWidthItem.getMenuComponents()) {
                final JRadioButtonMenuItem menuItem = (JRadioButtonMenuItem) component;
                if (menuItem.getName().equals(lineWidthType.name())) {
                    menuItem.setSelected(true);
                }
            }
        }
    }

    private void initShowHideSeriesItems() {
        showSeriesItem = new JMenuItem("Show Series");
        showSeriesItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                highlighted.setHidden(false);
            }
        });

        hideSeriesItem = new JMenuItem("Hide Series");
        hideSeriesItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                highlighted.setHidden(true);
            }
        });
    }

    private void initRendererItems() {
        priceRendererItem = new JMenu("Series Type");
        final ButtonGroup priceRendererGroup = new ButtonGroup();
        for (final PriceRendererType type : PriceRendererType.values()) {
            final JRadioButtonMenuItem item = new JRadioButtonMenuItem(type.toString());
            item.setName(type.name());
            item.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    updatePriceRendererType(type);
                }
            });
            priceRendererGroup.add(item);
            priceRendererItem.add(item);
        }

        seriesRendererItem = new JMenu("Series Type");
        final ButtonGroup seriesRendererGroup = new ButtonGroup();
        for (final SeriesRendererType type : SeriesRendererType.values()) {
            final JRadioButtonMenuItem item = new JRadioButtonMenuItem(type.toString());
            item.setName(type.name());
            item.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent e) {
                    final XYItemRenderer renderer = highlighted.getRenderer();
                    final Stroke stroke = renderer.getSeriesStroke(0);
                    final LineStyleType lineStyleType = LineStyleType.valueOf(stroke);
                    final LineWidthType lineWidthType = LineWidthType.valueOf(stroke);
                    final Color color = (Color) renderer.getSeriesPaint(0);
                    highlighted.setRenderer(type.newRenderer(lineStyleType, lineWidthType, color));
                }
            });
            seriesRendererGroup.add(item);
            seriesRendererItem.add(item);
        }
        volumeRendererItem = new JRadioButtonMenuItem("Volume");
        volumeRendererItem.setName("Volume");
        volumeRendererItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                final XYItemRenderer renderer = highlighted.getRenderer();
                final CustomVolumeBarRenderer newRenderer = getVolumeRenderer();
                newRenderer.setSeriesPaint(0, renderer.getSeriesPaint(0));
                newRenderer.setSeriesStroke(0, renderer.getSeriesStroke(0));
                highlighted.setRenderer(newRenderer);
            }
        });
        seriesRendererGroup.add(volumeRendererItem);
        seriesRendererItem.add(volumeRendererItem);
    }

    private void initStrokeItems() {
        final ButtonGroup lineStyleGroup = new ButtonGroup();
        lineStyleItem = new JMenu("Line Style");
        for (final LineStyleType type : LineStyleType.values()) {
            final JRadioButtonMenuItem item = new JRadioButtonMenuItem(type.toString());
            item.setName(type.name());
            item.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    final XYItemRenderer renderer = highlighted.getRenderer();
                    final Stroke stroke = renderer.getSeriesStroke(0);
                    renderer.setSeriesStroke(0, type.getStroke(stroke));
                }
            });
            lineStyleGroup.add(item);
            lineStyleItem.add(item);
        }

        final ButtonGroup lineWidthGroup = new ButtonGroup();
        lineWidthItem = new JMenu("Line Width");
        for (final LineWidthType type : LineWidthType.values()) {
            final JRadioButtonMenuItem item = new JRadioButtonMenuItem(type.toString());
            item.setName(type.name());
            item.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent e) {
                    final XYItemRenderer renderer = highlighted.getRenderer();
                    final Stroke stroke = renderer.getSeriesStroke(0);
                    renderer.setSeriesStroke(0, type.getStroke(stroke));
                }

            });
            lineWidthGroup.add(item);
            lineWidthItem.add(item);
        }
    }

    private void initExportItems() {
        copyToClipboardItem = new JMenuItem("Copy To Clipboard");
        copyToClipboardItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                copyToClipboard();
            }
        });

        saveAsPNGItem = new JMenuItem("Save As PNG...");
        saveAsPNGItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                saveAsPNG();
            }
        });
    }

    private void initColorItems() {
        upColorItem = new JMenuItem("Change Up Color");
        upColorItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final IUpDownColorRenderer renderer = (IUpDownColorRenderer) highlighted.getRenderer();
                showColorChooserDialog("Up Color", renderer.getUpColor(), new IColorChooserListener() {
                    @Override
                    public void change(final Color initialColor, final Color newColor) {
                        renderer.setUpColor(newColor);
                    }

                    @Override
                    public void ok(final Color initialColor, final Color acceptedColor) {
                        renderer.setUpColor(acceptedColor);
                    }

                    @Override
                    public void cancel(final Color initialColor, final Color cancelledColor) {
                        renderer.setUpColor(initialColor);
                    }
                });
            }
        });
        downColorItem = new JMenuItem("Change Down Color");
        downColorItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final IUpDownColorRenderer renderer = (IUpDownColorRenderer) highlighted.getRenderer();
                showColorChooserDialog("Down Color", renderer.getDownColor(), new IColorChooserListener() {
                    @Override
                    public void change(final Color initialColor, final Color newColor) {
                        renderer.setDownColor(newColor);
                    }

                    @Override
                    public void ok(final Color initialColor, final Color acceptedColor) {
                        renderer.setDownColor(acceptedColor);
                    }

                    @Override
                    public void cancel(final Color initialColor, final Color cancelledColor) {
                        renderer.setDownColor(initialColor);
                    }
                });
            }
        });
        colorItem = new JMenuItem("Change Color");
        colorItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final XYItemRenderer renderer = highlighted.getRenderer();
                showColorChooserDialog("Color", (Color) renderer.getSeriesPaint(0), new IColorChooserListener() {
                    @Override
                    public void change(final Color initialColor, final Color newColor) {
                        renderer.setSeriesPaint(0, newColor);
                    }

                    @Override
                    public void ok(final Color initialColor, final Color acceptedColor) {
                        renderer.setSeriesPaint(0, acceptedColor);
                    }

                    @Override
                    public void cancel(final Color initialColor, final Color cancelledColor) {
                        renderer.setSeriesPaint(0, initialColor);
                    }
                });
            }
        });
    }

    protected void showColorChooserDialog(final String name, final Color initialColor,
            final IColorChooserListener listener) {
        Dialogs.showColorChooserDialog(chartPanel, name, initialColor, true, listener);
    }

    private void initHelpItem() {
        helpItem = new JMenuItem("Help");
        helpItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final StringBuilder sb = new StringBuilder();
                sb.append("<html><h2>Usage:</h2>");
                sb.append("<ul>");
                sb.append("<li><b>Show/Hide Series</b>: Left click a series legend to show/hide that series."
                        + "<br>Or you can also use the series diplay settings context menu for that.</li>");
                sb.append(
                        "<li><b>Series Display Settings</b>: Right click a series legend to get a context menu to modify its display settings."
                                + "<br>You can modify the series type, line style, line width and colors. The settings differ depending on the selected series type."
                                + "<br>You can also show/hide series using the context menu, though only visible series can be modified in style."
                                + "<br>The series style settings can also be reset to their initial values using the context menu.</li>");
                sb.append("<li><b>Moving Series</b>: Drag and drop a series legend to move it to a different plot pane."
                        + "<br>The green pane will add a new plot. The red trash pane will remove the series (only visible if the series is actually trashable)."
                        + "<br>You can combine multiple series into one plot pane. Empty plots will be removed automatically which is displayed in red as well.</li>");
                sb.append(
                        "<li><b>Plot Pane Resizing</b>: Drag and drop a divider between two plot panes to change the size of those plots.</li>");
                sb.append(
                        "<li><b>Crosshair</b>: Move the mouse around with see the selected series values displayed in the series legends."
                                + "<br>If there is no crosshair visible, the latest values will be shown in the series legends.</li>");
                sb.append(
                        "<li><b>Navigation</b>: Move the mouse to the bottom center of the chart to show navigation buttons."
                                + "<br>The navigation also works by hotkeys and mouse.</li>");
                sb.append(
                        "<li><b>Panning</b>: Use left click and drag the mouse anywhere on the chart or use left/right arrow keys on your keyboard to pan the data."
                                + "<br>You can also use horizontal scrolling of your mouse or the shift key combined with your scroll wheel."
                                + "<br>By holding down the control key on your keyboard you can make the panning faster.</li>");
                sb.append(
                        "<li><b>Zooming</b>: Use your scroll wheel or +/- keys to zoom. When using the mouse to scroll, the mouse pointer will be used as an anchor.</li>");
                sb.append(
                        "<li><b>Exporting</b>: Right click anywhere on the chart to get a context menu that allows you export the chart image to clipboard or to a file."
                                + "<br>Right click multiple times to remove the crosshair or just use the settings navigation button on the bottom of the chart.</li>");
                sb.append("</ul>");
                sb.append("</html>");
                Dialogs.showMessageDialog(chartPanel, sb.toString(), "Help", Dialogs.PLAIN_MESSAGE);
            }
        });
    }

    private boolean isVolumeSeries(final HighlightedLegendInfo highlighted) {
        if (highlighted.getRenderer() == getVolumeRenderer()) {
            volumeSeriesKeys.add(highlighted.getSeriesKey());
            return true;
        }
        return volumeSeriesKeys.contains(highlighted.getSeriesKey());
    }

    public void displayPopupMenu(final int x, final int y) {
        this.popupMenu.show(chartPanel, x, y);
    }

    public void copyToClipboard() {
        final Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        final Insets insets = chartPanel.getChartPanel().getInsets();
        final int w = chartPanel.getChartPanel().getWidth() - insets.left - insets.right;
        final int h = chartPanel.getChartPanel().getHeight() - insets.top - insets.bottom;
        final CustomChartTransferable selection = new CustomChartTransferable(chartPanel.getChart(), w, h,
                chartPanel.getChartPanel().getMinimumDrawWidth(), chartPanel.getChartPanel().getMinimumDrawHeight(),
                chartPanel.getChartPanel().getMaximumDrawWidth(), chartPanel.getChartPanel().getMaximumDrawHeight());
        systemClipboard.setContents(selection, null);
    }

    public void saveAsPNG() {
        final JFileChooser fileChooser = new JFileChooser();
        final FileNameExtensionFilter filter = new FileNameExtensionFilter(".png", "png");
        fileChooser.addChoosableFileFilter(filter);
        fileChooser.setFileFilter(filter);

        final int option = fileChooser.showSaveDialog(chartPanel);
        if (option == JFileChooser.APPROVE_OPTION) {
            String filename = fileChooser.getSelectedFile().getPath();
            if (!Strings.endsWithIgnoreCase(filename, ".png")) {
                filename = filename + ".png";
            }
            try {
                ChartUtils.saveChartAsPNG(new File(filename), chartPanel.getChart(),
                        chartPanel.getChartPanel().getWidth(), chartPanel.getChartPanel().getHeight());
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void mousePressed(final MouseEvent e) {
        mouseReleased(e);
    }

    public void mouseReleased(final MouseEvent e) {
        if (e.isPopupTrigger()) {
            displayPopupMenu(e.getX(), e.getY());
        }
    }

    public boolean isShowing() {
        return popupMenu.isShowing();
    }

    public void removeSeries(final String seriesKey) {
        seriesKey_initialSeriesSettings.remove(seriesKey);
        volumeSeriesKeys.remove(seriesKey);
    }
}
