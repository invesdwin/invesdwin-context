package de.invesdwin.context.jfreechart.panel.helper.config;

import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.concurrent.NotThreadSafe;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.jfree.chart.ChartUtils;

import de.invesdwin.context.jfreechart.panel.InteractiveChartPanel;
import de.invesdwin.context.jfreechart.panel.basis.CustomChartTransferable;
import de.invesdwin.context.jfreechart.panel.helper.config.dialog.SettingsDialog;
import de.invesdwin.context.jfreechart.panel.helper.legend.HighlightedLegendInfo;
import de.invesdwin.util.lang.Strings;
import de.invesdwin.util.swing.Dialogs;
import de.invesdwin.util.swing.listener.PopupMenuListenerSupport;

@NotThreadSafe
public class PlotConfigurationHelper {

    private final InteractiveChartPanel chartPanel;

    private final PriceInitialSettings priceInitialSettings;
    private final Map<String, SeriesInitialSettings> seriesKey_initialSettings = new HashMap<>();

    private JPopupMenu popupMenu;
    private JMenuItem titleItem;
    private HighlightedLegendInfo highlighted;

    private JMenuItem configureSeriesItem;
    private JMenuItem removeSeriesItem;
    private JMenuItem showSeriesItem;
    private JMenuItem hideSeriesItem;

    private JMenuItem copyToClipboardItem;
    private JMenuItem saveAsPNGItem;
    private JMenuItem helpItem;

    public PlotConfigurationHelper(final InteractiveChartPanel chartPanel) {
        this.chartPanel = chartPanel;
        initPopupMenu();

        priceInitialSettings = new PriceInitialSettings(this);
    }

    public JPopupMenu getPopupMenu() {
        return popupMenu;
    }

    public InteractiveChartPanel getChartPanel() {
        return chartPanel;
    }

    public PriceInitialSettings getPriceInitialSettings() {
        return priceInitialSettings;
    }

    private void initPopupMenu() {

        titleItem = new JMenuItem("");
        titleItem.setEnabled(false);

        initSeriesVisibilityItems();
        initExportItems();
        initHelpItem();

        popupMenu = new JPopupMenu();
        popupMenu.addPopupMenuListener(new PopupMenuListenerSupport() {

            @Override
            public void popupMenuWillBecomeVisible(final PopupMenuEvent e) {
                popupMenu.removeAll();
                highlighted = chartPanel.getPlotLegendHelper().getHighlightedLegendInfo();
                if (highlighted != null) {
                    if (!highlighted.isDatasetVisible()) {
                        popupMenu.add(titleItem);
                        if (highlighted.isRemovable()) {
                            popupMenu.add(removeSeriesItem);
                        }
                        popupMenu.add(showSeriesItem);
                    } else {
                        addSeriesConfigMenuItems();
                    }
                } else {
                    popupMenu.add(copyToClipboardItem);
                    popupMenu.add(saveAsPNGItem);
                    popupMenu.addSeparator();
                    popupMenu.add(helpItem);
                }

                chartPanel.getPlotNavigationHelper().mouseExited();
            }

            private void addSeriesConfigMenuItems() {
                if (highlighted.isPriceSeries()) {
                    titleItem.setText("Series - " + String.valueOf(chartPanel.getDataset().getSeriesKey(0)));
                } else {
                    titleItem.setText("Series - " + highlighted.getSeriesKey());
                }
                popupMenu.add(titleItem);
                popupMenu.addSeparator();
                popupMenu.add(configureSeriesItem);
                if (highlighted.isRemovable()) {
                    popupMenu.add(removeSeriesItem);
                }
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

    public SeriesInitialSettings getOrCreateSeriesInitialSettings(final HighlightedLegendInfo highlighted) {
        SeriesInitialSettings seriesInitialSettings = seriesKey_initialSettings.get(highlighted.getSeriesKey());
        if (seriesInitialSettings == null) {
            seriesInitialSettings = new SeriesInitialSettings(highlighted.getRenderer());
            seriesKey_initialSettings.put(highlighted.getSeriesKey(), seriesInitialSettings);
        }
        return seriesInitialSettings;
    }

    public SeriesInitialSettings getSeriesInitialSettings(final HighlightedLegendInfo highlighted) {
        return seriesKey_initialSettings.get(highlighted.getSeriesKey());
    }

    private void initSeriesVisibilityItems() {
        configureSeriesItem = new JMenuItem("Configure");
        configureSeriesItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final SettingsDialog dialog = new SettingsDialog(PlotConfigurationHelper.this, highlighted);
                dialog.setVisible(true);
            }
        });

        removeSeriesItem = new JMenuItem("Remove");
        removeSeriesItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                highlighted.removeSeries();
            }
        });

        showSeriesItem = new JMenuItem("Show");
        showSeriesItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                highlighted.setDatasetVisible(true);
            }
        });

        hideSeriesItem = new JMenuItem("Hide");
        hideSeriesItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                highlighted.setDatasetVisible(false);
            }
        });
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

    private void initHelpItem() {
        helpItem = new JMenuItem("Help");
        helpItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final StringBuilder sb = new StringBuilder();
                sb.append("<html><h2>Usage:</h2>");
                sb.append("<ul>");
                sb.append(
                        "<li><b>Move Series</b>: Drag and drop a series legend to move it to a different plot pane. The green pane will add a new plot. "
                                + "<br>On removable series the red trash pane will remove it. You can combine multiple series into one plot pane."
                                + "<br>Empty plots will be removed automatically which is displayed in red as well.</li>");
                sb.append(
                        "<li><b>Show/Hide Series</b>: Left click a series legend to show/hide that series. Or you can also use the series context menu for that.</li>");
                sb.append(
                        "<li><b>Series Context Menu</b>: Right click a series legend to get a context menu to modify its display settings."
                                + "<br>You can modify the series type, line style, line width and colors. The settings differ depending on the selected series type."
                                + "<br>The series style settings can also be reset to their initial values. If the series is removable, you can remove it here too."
                                + "<br>You can also show/hide the series using the context menu, though only non hidden series can be modified in style.</li>");
                sb.append(
                        "<li><b>Plot Pane Resizing</b>: Drag and drop a divider between two plot panes to change the size of those.</li>");
                sb.append(
                        "<li><b>Crosshair</b>: Move the mouse around with see the selected series values displayed in the series legends."
                                + "<br>If there is no crosshair visible, the latest values will be shown in the series legends.</li>");
                sb.append(
                        "<li><b>Navigation</b>: Move the mouse to the bottom center of the chart to show navigation buttons."
                                + "<br>This allows you to pan, zoom and reset the view. The navigation also works by hotkeys and mouse:");
                sb.append("<ul>");
                sb.append(
                        "<li><b>Panning</b>: Use left click and drag the mouse anywhere on the chart or use left/right arrow keys on your keyboard to pan the data."
                                + "<br>You can also use horizontal scrolling of your mouse or the shift key combined with your scroll wheel."
                                + "<br>By holding down the control key on your keyboard you can make the panning faster.</li>");
                sb.append(
                        "<li><b>Zooming</b>: Use your scroll wheel or +/- keys to zoom. When using the mouse to scroll, the mouse pointer will be used as an anchor.</li>");
                sb.append("</ul></li>");
                sb.append(
                        "<li><b>Exporting</b>: Right click anywhere on the chart to get a context menu that allows you export the chart image to clipboard or to a file."
                                + "<br>Right click multiple times to remove the crosshair or just use the settings navigation button on the bottom of the chart.</li>");
                sb.append("</ul>");
                sb.append("</html>");
                Dialogs.showMessageDialog(chartPanel, sb.toString(), "Help", Dialogs.PLAIN_MESSAGE);
            }
        });
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

    public void removeInitialSeriesSettings(final String seriesKey) {
        seriesKey_initialSettings.remove(seriesKey);
    }
}
