package de.invesdwin.context.jfreechart.panel.helper.config.series;

import de.invesdwin.context.jfreechart.panel.helper.config.dialog.parameter.ParameterSettingsPanel;
import de.invesdwin.context.jfreechart.panel.helper.config.dialog.parameter.modifier.IParameterSettingsModifier;
import de.invesdwin.util.math.expression.IExpression;

public interface ISeriesParameter {

    String getName();

    String getDescription();

    IExpression getDefaultValue();

    SeriesParameterType getType();

    IExpression[] getEnumerationValues();

    default IParameterSettingsModifier newModifier(final ParameterSettingsPanel panel) {
        return getType().newModifier(this, panel);
    }

}
