package de.invesdwin.context.jfreechart.panel.helper;

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
import de.invesdwin.context.jfreechart.renderer.CustomOhlcBarRenderer;
import de.invesdwin.context.jfreechart.renderer.CustomOhlcCandlestickRenderer;
import de.invesdwin.context.jfreechart.renderer.CustomVolumeBarRenderer;
import de.invesdwin.context.jfreechart.renderer.CustomXYAreaRenderer;
import de.invesdwin.util.error.UnknownArgumentException;
import de.invesdwin.util.lang.Colors;
import de.invesdwin.util.lang.Strings;

@NotThreadSafe
public class PlotConfigurationHelper {

    public static final Color DEFAULT_DOWN_COLOR = Colors.fromHex("#EF5350");
    public static final Color DEFAULT_UP_COLOR = Colors.fromHex("#26A69A");
    public static final Color DEFAULT_PRICE_COLOR = Colors.fromHex("#3C78D8");
    public static final int VOLUME_BAR_ALPHA = 100;
    public static final int AREA_FILL_ALPHA = 25;

    private final InteractiveChartPanel chartPanel;
    private final JPopupMenu popupMenu;

    private PriceRendererType priceRendererType = PriceRendererType.Candlesticks;
    private Color upColor;
    private Color downColor;
    private Color priceColor;

    private final CustomOhlcCandlestickRenderer candlestickRenderer;
    private final CustomOhlcBarRenderer barsRenderer;
    private final CustomVolumeBarRenderer volumeBarRenderer;
    private final CustomXYAreaRenderer areaRenderer;
    private final StandardXYItemRenderer lineRenderer;
    private final XYStepRenderer stepLineRenderer;

    private final Set<String> volumeSeriesKeys = new HashSet<>();

    private JMenuItem titleItem;
    private JMenu priceRendererItem;
    private JMenu seriesRendererItem;
    private JMenuItem copyToClipboardItem;
    private JMenuItem saveAsPNGItem;
    private HighlightedLegendInfo highlighted;

    public PlotConfigurationHelper(final InteractiveChartPanel chartPanel) {
        this.chartPanel = chartPanel;
        this.popupMenu = createPopupMenu();

        this.candlestickRenderer = new CustomOhlcCandlestickRenderer(chartPanel.getDataset());
        this.barsRenderer = new CustomOhlcBarRenderer(candlestickRenderer);
        this.volumeBarRenderer = new CustomVolumeBarRenderer(candlestickRenderer);
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

    public CustomVolumeBarRenderer getVolumeBarRenderer() {
        return volumeBarRenderer;
    }

    public Color getUpColor() {
        return upColor;
    }

    public void setUpColor(final Color upColor) {
        this.upColor = upColor;

        candlestickRenderer.setUpPaint(upColor);
        final Color upColorAlpha = new Color(upColor.getRed(), upColor.getGreen(), upColor.getBlue(), VOLUME_BAR_ALPHA);
        volumeBarRenderer.setUpColor(upColorAlpha);
    }

    public Color getDownColor() {
        return downColor;
    }

    public void setDownColor(final Color downColor) {
        this.downColor = downColor;

        candlestickRenderer.setDownPaint(downColor);
        final Color downColorAlpha = new Color(downColor.getRed(), downColor.getGreen(), downColor.getBlue(),
                VOLUME_BAR_ALPHA);
        volumeBarRenderer.setDownColor(downColorAlpha);
    }

    public Color getPriceColor() {
        return priceColor;
    }

    public void setPriceColor(final Color priceColor) {
        this.priceColor = priceColor;

        this.lineRenderer.setSeriesPaint(0, priceColor);
        this.areaRenderer.setSeriesPaint(0, priceColor);
        this.volumeBarRenderer.setSeriesPaint(0, priceColor);
        this.stepLineRenderer.setSeriesPaint(0, priceColor);
        final Color priceColorAlpha = new Color(priceColor.getRed(), priceColor.getGreen(), priceColor.getBlue(),
                AREA_FILL_ALPHA);
        this.areaRenderer.setSeriesFillPaint(0, priceColorAlpha);
    }

    protected JPopupMenu createPopupMenu() {

        titleItem = new JMenuItem("");
        titleItem.setEnabled(false);

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
        for (final SeriesRendererType type : SeriesRendererType.values()) {
            final JRadioButtonMenuItem item = new JRadioButtonMenuItem(type.toString());
            item.setName(type.name());
            item.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    final XYItemRenderer renderer = highlighted.getRenderer();
                    if (type == SeriesRendererType.Column && volumeSeriesKeys.contains(highlighted.getSeriesKey())) {
                        final CustomVolumeBarRenderer newRenderer = getVolumeBarRenderer();
                        newRenderer.setSeriesPaint(0, renderer.getSeriesPaint(0));
                        highlighted.setRenderer(newRenderer);
                    } else {
                        final StrokeType strokeType = StrokeType.valueOf(renderer.getSeriesStroke(0));
                        final Color color = (Color) renderer.getSeriesPaint(0);
                        highlighted.setRenderer(type.newRenderer(strokeType, color));
                    }
                }
            });
            seriesRendererGroup.add(item);
            seriesRendererItem.add(item);
        }

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

        final JPopupMenu menu = new JPopupMenu();
        menu.addPopupMenuListener(new PopupMenuListener() {

            @Override
            public void popupMenuWillBecomeVisible(final PopupMenuEvent e) {
                menu.removeAll();
                highlighted = chartPanel.getPlotLegendHelper().getHighlightedLegendInfo();
                final JMenu rendererItem;
                if (highlighted == null || highlighted.isPriceSeries()) {
                    titleItem.setText(String.valueOf(chartPanel.getDataset().getSeriesKey(0)));
                    rendererItem = priceRendererItem;
                    for (final Component component : priceRendererItem.getMenuComponents()) {
                        final JRadioButtonMenuItem menuItem = (JRadioButtonMenuItem) component;
                        if (menuItem.getName().equals(priceRendererType.name())) {
                            menuItem.setSelected(true);
                        }
                    }
                } else {
                    if (highlighted.getRenderer() == getVolumeBarRenderer()) {
                        volumeSeriesKeys.add(highlighted.getSeriesKey());
                    }
                    titleItem.setText(highlighted.getSeriesKey());
                    rendererItem = seriesRendererItem;
                    final SeriesRendererType seriesRendererType = SeriesRendererType.valueOf(highlighted.getRenderer());
                    for (final Component component : seriesRendererItem.getMenuComponents()) {
                        final JRadioButtonMenuItem menuItem = (JRadioButtonMenuItem) component;
                        if (menuItem.getName().equals(seriesRendererType.name())) {
                            menuItem.setSelected(true);
                        }
                    }
                }
                menu.add(titleItem);
                menu.add(rendererItem);
                menu.addSeparator();
                menu.add(copyToClipboardItem);
                menu.add(saveAsPNGItem);

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

        return menu;

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
