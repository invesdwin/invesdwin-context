package de.invesdwin.context.jfreechart.panel.helper.config.dialog.indicator.modifier;

import javax.swing.JComponent;

import de.invesdwin.context.jfreechart.panel.helper.config.series.indicator.IIndicatorSeriesParameter;
import de.invesdwin.util.math.expression.IExpression;

public interface IParameterSettingsModifier {

    IIndicatorSeriesParameter getParameter();

    IExpression getValue();

    void setValue(IExpression value);

    JComponent getComponent();

}
