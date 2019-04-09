package de.invesdwin.context.jfreechart.panel.helper.config.series;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.context.jfreechart.panel.helper.config.dialog.parameter.modifier.BooleanParameterSettingsModifier;
import de.invesdwin.context.jfreechart.panel.helper.config.dialog.parameter.modifier.DoubleParameterSettingsModifier;
import de.invesdwin.context.jfreechart.panel.helper.config.dialog.parameter.modifier.EnumerationParameterSettingsModifier;
import de.invesdwin.context.jfreechart.panel.helper.config.dialog.parameter.modifier.IParameterSettingsModifier;
import de.invesdwin.context.jfreechart.panel.helper.config.dialog.parameter.modifier.IntegerParameterSettingsModifier;

@Immutable
public enum SeriesParameterType {
    Integer {
        @Override
        public IParameterSettingsModifier newModifier(final ISeriesParameter parameter) {
            return new IntegerParameterSettingsModifier(parameter);
        }
    },
    Double {
        @Override
        public IParameterSettingsModifier newModifier(final ISeriesParameter parameter) {
            return new DoubleParameterSettingsModifier(parameter);
        }
    },
    Boolean {
        @Override
        public IParameterSettingsModifier newModifier(final ISeriesParameter parameter) {
            return new BooleanParameterSettingsModifier(parameter);
        }
    },
    Enumeration {
        @Override
        public IParameterSettingsModifier newModifier(final ISeriesParameter parameter) {
            return new EnumerationParameterSettingsModifier(parameter);
        }
    };

    public abstract IParameterSettingsModifier newModifier(ISeriesParameter parameter);
}
