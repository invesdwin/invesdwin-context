package de.invesdwin.context.jfreechart.panel.helper.config.dialog.parameter.modifier;

import javax.annotation.concurrent.NotThreadSafe;
import javax.swing.JComponent;
import javax.swing.JTextField;

import de.invesdwin.context.jfreechart.panel.helper.config.dialog.parameter.ParameterSettingsPanel;
import de.invesdwin.context.jfreechart.panel.helper.config.series.ISeriesParameter;
import de.invesdwin.util.math.expression.IExpression;

@NotThreadSafe
public class BooleanParameterSettingsModifier implements IParameterSettingsModifier {

    public BooleanParameterSettingsModifier(final ISeriesParameter parameter, final ParameterSettingsPanel panel) {}

    @Override
    public ISeriesParameter getParameter() {
        return null;
    }

    @Override
    public IExpression getValue() {
        return null;
    }

    @Override
    public void setValue(final IExpression value) {}

    @Override
    public JComponent newComponent() {
        return new JTextField();
    }

}
