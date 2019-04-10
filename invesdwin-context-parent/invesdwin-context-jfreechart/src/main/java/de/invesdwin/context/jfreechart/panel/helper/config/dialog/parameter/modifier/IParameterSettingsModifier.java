package de.invesdwin.context.jfreechart.panel.helper.config.dialog.parameter.modifier;

import javax.swing.JComponent;

import de.invesdwin.context.jfreechart.panel.helper.config.series.ISeriesParameter;
import de.invesdwin.util.math.expression.IExpression;

public interface IParameterSettingsModifier {

    ISeriesParameter getParameter();

    IExpression getValue();

    void setValue(IExpression value);

    JComponent getComponent();

}
