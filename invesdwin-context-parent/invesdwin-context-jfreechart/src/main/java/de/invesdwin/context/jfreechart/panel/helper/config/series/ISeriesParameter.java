package de.invesdwin.context.jfreechart.panel.helper.config.series;

import de.invesdwin.util.math.expression.IExpression;

public interface ISeriesParameter {

    String getName();

    String getDescription();

    IExpression getDefaultValue();

    SeriesParameterType getType();

    IExpression[] getEnumerationValues();

}
