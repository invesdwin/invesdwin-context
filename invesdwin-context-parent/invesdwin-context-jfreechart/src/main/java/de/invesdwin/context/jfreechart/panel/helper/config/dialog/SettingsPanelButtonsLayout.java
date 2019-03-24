package de.invesdwin.context.jfreechart.panel.helper.config.dialog;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.annotation.concurrent.NotThreadSafe;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

@NotThreadSafe
public class SettingsPanelButtonsLayout extends JPanel {

    //CHECKSTYLE:OFF
    public final JButton btn_reset;
    public final JButton btn_cancel;
    public final JButton btn_ok;
    //CHECKSTYLE:ON

    public SettingsPanelButtonsLayout() {
        setBorder(new EmptyBorder(0, 10, 5, 10));

        final GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] { 80, 0, 80, 80, 0 };
        gridBagLayout.rowHeights = new int[] { 25, 0 };
        gridBagLayout.columnWeights = new double[] { 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE };
        gridBagLayout.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
        setLayout(gridBagLayout);
        btn_reset = new JButton("Reset");

        final GridBagConstraints gbc_btn_reset = new GridBagConstraints();
        gbc_btn_reset.fill = GridBagConstraints.HORIZONTAL;
        gbc_btn_reset.anchor = GridBagConstraints.NORTH;
        gbc_btn_reset.insets = new Insets(0, 0, 0, 5);
        gbc_btn_reset.gridx = 0;
        gbc_btn_reset.gridy = 0;
        add(btn_reset, gbc_btn_reset);
        btn_cancel = new JButton("Cancel");
        final GridBagConstraints gbc_btn_cancel = new GridBagConstraints();
        gbc_btn_cancel.fill = GridBagConstraints.HORIZONTAL;
        gbc_btn_cancel.anchor = GridBagConstraints.NORTH;
        gbc_btn_cancel.insets = new Insets(0, 0, 0, 5);
        gbc_btn_cancel.gridx = 2;
        gbc_btn_cancel.gridy = 0;
        add(btn_cancel, gbc_btn_cancel);
        btn_ok = new JButton("Ok");
        final GridBagConstraints gbc_btn_ok = new GridBagConstraints();
        gbc_btn_ok.fill = GridBagConstraints.HORIZONTAL;
        gbc_btn_ok.anchor = GridBagConstraints.NORTH;
        gbc_btn_ok.gridx = 3;
        gbc_btn_ok.gridy = 0;
        add(btn_ok, gbc_btn_ok);
    }

}
