package de.invesdwin.context.jfreechart.panel.helper.config.series;

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
import de.invesdwin.util.swing.Dialogs;
import de.invesdwin.util.swing.listener.WindowListenerSupport;

@NotThreadSafe
public class AddSeriesDialog extends JDialog {

    private final AddSeriesPanel panel;

    public AddSeriesDialog(final PlotConfigurationHelper plotConfigurationHelper) {
        super(Dialogs.getFrameForComponent(plotConfigurationHelper.getChartPanel()), true);
        System.out.println("TODO: search box; "//
                + "add custom series providers (from strategy); "//
                + "add default renderer to metadata; "//
                + "remove automatic yellow color?; "//
                + "increase height of add series dialog; "//
                + "document this feature in help;"//
                + "add constant line series provider;"//
                + "add expression series provider or a menu point to add a custom expression;"//
                + "allow to plot expression components individually (only API or also via popup?)");
        final Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        panel = new AddSeriesPanel(plotConfigurationHelper, this);
        contentPane.add(panel);
        setTitle("Add Series");

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
        dispose();
    }
}
