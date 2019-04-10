package de.invesdwin.context.jfreechart.panel.helper.config.dialog.parameter.modifier;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.annotation.concurrent.NotThreadSafe;
import javax.swing.JComboBox;
import javax.swing.JComponent;

import de.invesdwin.context.jfreechart.panel.helper.config.series.ISeriesParameter;
import de.invesdwin.util.math.expression.IExpression;

@NotThreadSafe
public class EnumerationParameterSettingsModifier extends AParameterSettingsModifier {

    private final JComboBox<IExpression> component;

    public EnumerationParameterSettingsModifier(final ISeriesParameter parameter, final Runnable modificationListener) {
        super(parameter);
        component = new JComboBox<IExpression>();
        final IExpression[] values = parameter.getEnumerationValues();
        for (int i = 0; i < values.length; i++) {
            component.addItem(values[i]);
        }
        component.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                EnumerationParameterSettingsModifier.super.setValue((IExpression) component.getSelectedItem());
                modificationListener.run();
            }
        });
    }

    @Override
    public void setValue(final IExpression value) {
        super.setValue(value);
        component.setSelectedItem(value);
    }

    @Override
    public JComponent getComponent() {
        return component;
    }

}
