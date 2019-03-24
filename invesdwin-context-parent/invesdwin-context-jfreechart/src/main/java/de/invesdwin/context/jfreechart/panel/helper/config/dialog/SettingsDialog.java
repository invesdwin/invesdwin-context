package de.invesdwin.context.jfreechart.panel.helper.config.dialog;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.annotation.concurrent.NotThreadSafe;
import javax.swing.JDialog;

import de.invesdwin.context.jfreechart.panel.helper.config.PlotConfigurationHelper;
import de.invesdwin.context.jfreechart.panel.helper.legend.HighlightedLegendInfo;
import de.invesdwin.util.swing.Dialogs;

@NotThreadSafe
public class SettingsDialog extends JDialog {

    public SettingsDialog(final PlotConfigurationHelper plotConfigurationHelper,
            final HighlightedLegendInfo highlighted) {
        super(Dialogs.getFrameForComponent(plotConfigurationHelper.getChartPanel()), true);
        final Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(new SettingsPanel(plotConfigurationHelper, highlighted, this));
        setTitle("Series Settings - " + highlighted.getSeriesKey());
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setResizable(false);
        pack();
        setLocationRelativeTo(plotConfigurationHelper.getChartPanel());
    }

}
