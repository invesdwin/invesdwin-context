package de.invesdwin.context.jfreechart.panel.helper.config;

import java.awt.Color;
import java.awt.Component;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.concurrent.NotThreadSafe;
import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
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
import de.invesdwin.util.error.UnknownArgumentException;
import de.invesdwin.util.lang.Colors;
import de.invesdwin.util.lang.Strings;
import de.invesdwin.util.swing.Dialogs;

@NotThreadSafe
public class PlotConfigurationHelper {

    public static final Color DEFAULT_DOWN_COLOR = Colors.fromHex("#EF5350");
    public static final Color DEFAULT_UP_COLOR = Colors.fromHex("#26A69A");
    public static final Color DEFAULT_PRICE_COLOR = Colors.fromHex("#3C78D8");
    public static final int VOLUME_BAR_ALPHA = 100;
    public static final int AREA_FILL_ALPHA = 25;
    private static final String EYE_DROPPER_COLOR_CHOOSER_PANEL_CLASS = "org.jdesktop.swingx.color.EyeDropperColorChooserPanel";
    private static final String VOLUME_ITEM_NAME = "Volume";

    private final InteractiveChartPanel chartPanel;

    private PriceRendererType priceRendererType = PriceRendererType.Candlesticks;
    private Color upColor;
    private Color downColor;
    private Color priceColor;

    private final CustomOhlcCandlestickRenderer candlestickRenderer;
    private final CustomOhlcBarRenderer barsRenderer;
    private final CustomVolumeBarRenderer volumeRenderer;
    private final CustomXYAreaRenderer areaRenderer;
    private final StandardXYItemRenderer lineRenderer;
    private final XYStepRenderer stepLineRenderer;

    private final Set<String> volumeSeriesKeys = new HashSet<>();

    private JPopupMenu popupMenu;
    private JMenuItem titleItem;
    private JMenu priceRendererItem;
    private JMenu seriesRendererItem;
    private JMenuItem volumeRendererItem;
    private JMenuItem strokeItem;
    private JMenuItem upColorItem;
    private JMenuItem downColorItem;
    private JMenuItem colorItem;
    private JMenuItem copyToClipboardItem;
    private JMenuItem saveAsPNGItem;
    private JMenuItem helpItem;
    private HighlightedLegendInfo highlighted;

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
    }

    public JPopupMenu getPopupMenu() {
        return this.popupMenu;
    }

    public PriceRendererType getPriceRendererType() {
        return priceRendererType;
    }

    public void setPriceRendererType(final PriceRendererType priceRendererType) {
        if (priceRendererType != this.priceRendererType) {
            chartPanel.getOhlcPlot().setRenderer(0, getPriceRenderer(priceRendererType));
            chartPanel.update();
        }
        this.priceRendererType = priceRendererType;
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
        case Bars:
            return barsRenderer;
        case Candlesticks:
            return candlestickRenderer;
        default:
            throw UnknownArgumentException.newInstance(PriceRendererType.class, priceRendererType);
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

        candlestickRenderer.setUpColor(upColor);
        final Color upColorAlpha = new Color(upColor.getRed(), upColor.getGreen(), upColor.getBlue(), VOLUME_BAR_ALPHA);
        volumeRenderer.setUpColor(upColorAlpha);
    }

    public Color getDownColor() {
        return downColor;
    }

    public void setDownColor(final Color downColor) {
        this.downColor = downColor;

        candlestickRenderer.setDownColor(downColor);
        final Color downColorAlpha = new Color(downColor.getRed(), downColor.getGreen(), downColor.getBlue(),
                VOLUME_BAR_ALPHA);
        volumeRenderer.setDownColor(downColorAlpha);
    }

    public Color getPriceColor() {
        return priceColor;
    }

    public void setPriceColor(final Color priceColor) {
        this.priceColor = priceColor;

        this.lineRenderer.setSeriesPaint(0, priceColor);
        this.areaRenderer.setSeriesPaint(0, priceColor);
        this.volumeRenderer.setSeriesPaint(0, priceColor);
        this.stepLineRenderer.setSeriesPaint(0, priceColor);
        final Color priceColorAlpha = new Color(priceColor.getRed(), priceColor.getGreen(), priceColor.getBlue(),
                AREA_FILL_ALPHA);
        this.areaRenderer.setSeriesFillPaint(0, priceColorAlpha);
    }

    private void initPopupMenu() {

        titleItem = new JMenuItem("");
        titleItem.setEnabled(false);

        initRendererItems();
        initStrokeItem();
        initColorItems();
        initExportItems();
        initHelpItem();

        popupMenu = new JPopupMenu();
        popupMenu.addPopupMenuListener(new PopupMenuListener() {

            @Override
            public void popupMenuWillBecomeVisible(final PopupMenuEvent e) {
                popupMenu.removeAll();
                highlighted = chartPanel.getPlotLegendHelper().getHighlightedLegendInfo();
                if (highlighted != null) {
                    final JMenu rendererItem;
                    if (highlighted.isPriceSeries()) {
                        titleItem.setText(String.valueOf(chartPanel.getDataset().getSeriesKey(0)));
                        rendererItem = priceRendererItem;
                        for (final Component component : priceRendererItem.getMenuComponents()) {
                            final JRadioButtonMenuItem menuItem = (JRadioButtonMenuItem) component;
                            if (menuItem.getName().equals(priceRendererType.name())) {
                                menuItem.setSelected(true);
                            }
                        }
                        strokeItem.setVisible(priceRendererType.isStrokeConfigurable());
                        upColorItem.setVisible(priceRendererType.isUpColorConfigurable());
                        downColorItem.setVisible(priceRendererType.isDownColorConfigurable());
                        colorItem.setVisible(priceRendererType.isColorConfigurable());
                    } else {
                        final boolean volumeSeries = isVolumeSeries(highlighted);
                        volumeRendererItem.setVisible(volumeSeries);
                        titleItem.setText(highlighted.getSeriesKey());
                        rendererItem = seriesRendererItem;
                        final XYItemRenderer renderer = highlighted.getRenderer();
                        if (renderer == getVolumeRenderer()) {
                            volumeRendererItem.setSelected(true);
                            strokeItem.setVisible(false);
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
                            strokeItem.setVisible(seriesRendererType.isStrokeConfigurable());
                            upColorItem.setVisible(seriesRendererType.isUpColorConfigurable());
                            downColorItem.setVisible(seriesRendererType.isDownColorConfigurable());
                            colorItem.setVisible(seriesRendererType.isColorConfigurable());
                        }
                    }
                    popupMenu.add(titleItem);
                    popupMenu.add(rendererItem);
                    popupMenu.add(strokeItem);
                    popupMenu.add(upColorItem);
                    popupMenu.add(downColorItem);
                    popupMenu.add(colorItem);
                } else {
                    popupMenu.add(copyToClipboardItem);
                    popupMenu.add(saveAsPNGItem);
                    popupMenu.add(helpItem);
                }

                chartPanel.getPlotNavigationHelper().mouseExited();
            }

            @Override
            public void popupMenuWillBecomeInvisible(final PopupMenuEvent e) {}

            @Override
            public void popupMenuCanceled(final PopupMenuEvent e) {
                highlighted = null;
                //only the first popup should have the crosshair visible
                chartPanel.mouseExited();
            }
        });

    }

    private void initRendererItems() {
        priceRendererItem = new JMenu("Renderer");
        final ButtonGroup priceRendererGroup = new ButtonGroup();
        for (final PriceRendererType type : PriceRendererType.values()) {
            final JRadioButtonMenuItem item = new JRadioButtonMenuItem(type.toString());
            item.setName(type.name());
            item.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    setPriceRendererType(type);
                }
            });
            priceRendererGroup.add(item);
            priceRendererItem.add(item);
        }

        seriesRendererItem = new JMenu("Renderer");
        final ButtonGroup seriesRendererGroup = new ButtonGroup();
        volumeRendererItem = new JRadioButtonMenuItem(VOLUME_ITEM_NAME);
        volumeRendererItem.setName(VOLUME_ITEM_NAME);
        volumeRendererItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final XYItemRenderer renderer = highlighted.getRenderer();
                final CustomVolumeBarRenderer newRenderer = getVolumeRenderer();
                newRenderer.setSeriesPaint(0, renderer.getSeriesPaint(0));
                highlighted.setRenderer(newRenderer);
            }
        });
        seriesRendererGroup.add(volumeRendererItem);
        seriesRendererItem.add(volumeRendererItem);
        for (final SeriesRendererType type : SeriesRendererType.values()) {
            final JRadioButtonMenuItem item = new JRadioButtonMenuItem(type.toString());
            item.setName(type.name());
            item.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    final XYItemRenderer renderer = highlighted.getRenderer();
                    final StrokeType strokeType = StrokeType.valueOf(renderer.getSeriesStroke(0));
                    final Color color = (Color) renderer.getSeriesPaint(0);
                    highlighted.setRenderer(type.newRenderer(strokeType, color));
                }
            });
            seriesRendererGroup.add(item);
            seriesRendererItem.add(item);
        }
        System.out.println(
                "TODO fix stroke painting, automatically set fill paint or make it configurable for area, reset button for settings");
    }

    private void initStrokeItem() {
        final ButtonGroup strokeRendererGroup = new ButtonGroup();
        strokeItem = new JMenu("Stroke");
        for (final StrokeType type : StrokeType.values()) {
            final JRadioButtonMenuItem item = new JRadioButtonMenuItem(type.toString());
            item.setName(type.name());
            item.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    final XYItemRenderer renderer = highlighted.getRenderer();
                    renderer.setSeriesStroke(0, type.getStroke());
                }
            });
            strokeRendererGroup.add(item);
            strokeItem.add(item);
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
                final Color newUpColor = showColorChooserDialog("Up Color", renderer.getUpColor());
                if (newUpColor != null) {
                    renderer.setUpColor(newUpColor);
                }
            }
        });
        downColorItem = new JMenuItem("Change Down Color");
        downColorItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final IUpDownColorRenderer renderer = (IUpDownColorRenderer) highlighted.getRenderer();
                final Color newDownColor = showColorChooserDialog("Down Color", renderer.getDownColor());
                if (newDownColor != null) {
                    renderer.setDownColor(newDownColor);
                }
            }
        });
        colorItem = new JMenuItem("Change Color");
        colorItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final XYItemRenderer renderer = highlighted.getRenderer();
                final Color newColor = showColorChooserDialog("Color", (Color) renderer.getSeriesPaint(0));
                if (newColor != null) {
                    renderer.setSeriesPaint(0, newColor);
                }
            }
        });
    }

    protected Color showColorChooserDialog(final String name, final Color initialColor) {
        return Dialogs.showColorChooserDialog(chartPanel, name, initialColor, true);
    }

    private void initHelpItem() {
        helpItem = new JMenuItem("Help");
        helpItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final StringBuilder sb = new StringBuilder();
                sb.append("<html><h3>Usage:</h3>");
                sb.append("<ul>");
                sb.append("<li><b>Show/Hide Series</b>: Left click a series legend to show/hide that series.</li>");
                sb.append(
                        "<li><b>Series Display Settings</b>: Right click a series legend to get a context menu to modify its display settings."
                                + "<br>You can modify the renderer (series type), stroke (line style) and colors.</li>");
                sb.append(
                        "<li><b>Series Realignment</b>: Drag and drop a series legend to move it to a different plot pane."
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
                        "<li><b>Panning</b>: Use left click and drag the mouse anywhere on the chart or use left/right arrow keys on your keyboard to do the same."
                                + "<br>You can also use horizontal scrolling of your mouse or the shift key combined with your scroll wheel.</li>");
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

}
