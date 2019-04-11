package de.invesdwin.context.jfreechart.panel.helper.config.dialog.indicator.modifier;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.context.jfreechart.panel.helper.config.series.indicator.IIndicatorSeriesParameter;
import de.invesdwin.util.math.expression.IExpression;

@NotThreadSafe
public abstract class AParameterSettingsModifier implements IParameterSettingsModifier {

    private final IIndicatorSeriesParameter parameter;
    private IExpression value;

    public AParameterSettingsModifier(final IIndicatorSeriesParameter parameter) {
        this.parameter = parameter;
    }

    @Override
    public IIndicatorSeriesParameter getParameter() {
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
