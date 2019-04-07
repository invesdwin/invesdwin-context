package de.invesdwin.context.jfreechart.panel.helper.config.dialog.parameter;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;
import javax.swing.JLabel;
import javax.swing.JPanel;

@NotThreadSafe
public class ParameterSettingsPanelLayout extends JPanel {

    //CHECKSTYLE:OFF
    public final List<IParameterSettingsModifier> modifiers;
    //CHECKSTYLE:ON

    public ParameterSettingsPanelLayout(final List<IParameterSettingsModifier> modifiers) {
        this.modifiers = modifiers;

        setLayout(new FlowLayout());
        int rows = 0;
        for (int i = 0; i < modifiers.size(); i++) {
            final IParameterSettingsModifier modifier = modifiers.get(i);
            final JLabel label = new JLabel(modifier.getParameter().getName());
            label.setToolTipText(modifier.getParameter().getDescription());
            add(label);
            add(modifier.newComponent());
            rows++;
        }

        setLayout(new GridLayout(rows, 2, 5, 5));

    }

}
