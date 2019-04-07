package de.invesdwin.context.jfreechart.panel.helper.config.dialog.parameter;

import javax.swing.JComponent;

import de.invesdwin.context.jfreechart.panel.helper.config.series.ISeriesParameter;
import de.invesdwin.util.math.expression.IExpression;

public interface IParameterSettingsModifier {

    ISeriesParameter getParameter();

    IExpression getValue();

    JComponent newComponent();

}
