package de.invesdwin.context.jfreechart.panel.helper.config.dialog.parameter.modifier;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.context.jfreechart.panel.helper.config.series.ISeriesParameter;
import de.invesdwin.util.math.expression.IExpression;

@NotThreadSafe
public abstract class AParameterSettingsModifier implements IParameterSettingsModifier {

    private final ISeriesParameter parameter;
    private IExpression value;

    public AParameterSettingsModifier(final ISeriesParameter parameter) {
        this.parameter = parameter;
    }

    @Override
    public ISeriesParameter getParameter() {
        return parameter;
    }

    @Override
    public IExpression getValue() {
        return value;
    }

    @Override
    public void setValue(final IExpression value) {
        this.value = value;
    }

}
