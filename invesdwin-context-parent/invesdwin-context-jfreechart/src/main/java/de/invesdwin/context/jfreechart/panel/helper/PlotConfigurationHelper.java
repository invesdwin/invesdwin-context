package de.invesdwin.context.jfreechart.panel.helper;

import java.awt.Insets;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

import javax.annotation.concurrent.NotThreadSafe;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.jfree.chart.ChartUtils;

import de.invesdwin.context.jfreechart.panel.InteractiveChartPanel;
import de.invesdwin.context.jfreechart.panel.basis.CustomChartTransferable;
import de.invesdwin.util.lang.Strings;

@NotThreadSafe
public class PlotConfigurationHelper {

    private final InteractiveChartPanel chartPanel;
    private final JPopupMenu popupMenu;

    public PlotConfigurationHelper(final InteractiveChartPanel chartPanel) {
        this.chartPanel = chartPanel;
        this.popupMenu = createPopupMenu();
    }

    public JPopupMenu getPopupMenu() {
        return this.popupMenu;
    }

    protected JPopupMenu createPopupMenu() {

        final JPopupMenu result = new JPopupMenu("Chart:");
        result.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(final PopupMenuEvent e) {}

            @Override
            public void popupMenuWillBecomeInvisible(final PopupMenuEvent e) {
                if (!isMouseOverChart()) {
                    chartPanel.mouseExited();
                }
            }

            @Override
            public void popupMenuCanceled(final PopupMenuEvent e) {}
        });

        //copy
        final JMenuItem copyItem = new JMenuItem("Copy To Clipboard");
        copyItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                copyToClipboard();
            }
        });
        result.add(copyItem);

        //save
        result.addSeparator();
        final JMenuItem pngItem = new JMenuItem("Save As PNG...");
        pngItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                saveAsPNG();
            }
        });
        result.add(pngItem);

        return result;

    }

    private boolean isMouseOverChart() {
        final Point mouseLocation = MouseInfo.getPointerInfo().getLocation();
        final Point chartPanelLocation = chartPanel.getLocationOnScreen();
        return mouseLocation.x >= chartPanelLocation.x
                && mouseLocation.x <= chartPanelLocation.x + chartPanel.getWidth()
                && mouseLocation.y >= chartPanelLocation.y
                && mouseLocation.y <= chartPanelLocation.y + chartPanel.getHeight();
    }

    public void displayPopupMenu(final int x, final int y) {
        this.popupMenu.show(chartPanel, x, y);
    }

    public void copyToClipboard() {
        //hide navigation on screenshot
        chartPanel.getPlotNavigationHelper().mouseExited();

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
        //hide navigation on screenshot
        chartPanel.getPlotNavigationHelper().mouseExited();

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
