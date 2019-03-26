package de.invesdwin.context.jfreechart.panel.helper.config.indicator;

import de.invesdwin.util.math.expression.IExpression;

public interface IIndicatorSeriesParameter {

    String getName();

    String getDescription();

    IExpression getDefaultValue();

    IndicatorSeriesParameterType getType();

    IExpression[] getEnumerationValues();

}
