package de.invesdwin.context.jfreechart.panel.helper.config.dialog;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;

import javax.annotation.concurrent.NotThreadSafe;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.KeyStroke;

import de.invesdwin.context.jfreechart.panel.helper.config.PlotConfigurationHelper;
import de.invesdwin.context.jfreechart.panel.helper.legend.HighlightedLegendInfo;
import de.invesdwin.util.swing.Dialogs;
import de.invesdwin.util.swing.listener.WindowListenerSupport;

@NotThreadSafe
public class SettingsDialog extends JDialog {

    private final SettingsPanel panel;

    public SettingsDialog(final PlotConfigurationHelper plotConfigurationHelper,
            final HighlightedLegendInfo highlighted) {
        super(Dialogs.getFrameForComponent(plotConfigurationHelper.getChartPanel()), true);
        final Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        panel = new SettingsPanel(plotConfigurationHelper, highlighted, this);
        contentPane.add(panel);
        setTitle(highlighted.getSeriesKey() + " - Series Settings");

        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowListenerSupport() {
            @Override
            public void windowClosing(final WindowEvent e) {
                close();
            }

        });

        getRootPane().registerKeyboardAction(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                close();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

        setResizable(false);
        pack();
        setLocationRelativeTo(plotConfigurationHelper.getChartPanel());
    }

    protected void close() {
        //tradingview also regards this as ok()
        panel.ok();
        dispose();
    }
}
