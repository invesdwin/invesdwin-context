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
        public IParameterSettingsModifier newModifier(final ISeriesParameter parameter,
                final Runnable modificationListener) {
            return new IntegerParameterSettingsModifier(parameter, modificationListener);
        }
    },
    Double {
        @Override
        public IParameterSettingsModifier newModifier(final ISeriesParameter parameter,
                final Runnable modificationListener) {
            return new DoubleParameterSettingsModifier(parameter, modificationListener);
        }
    },
    Boolean {
        @Override
        public IParameterSettingsModifier newModifier(final ISeriesParameter parameter,
                final Runnable modificationListener) {
            return new BooleanParameterSettingsModifier(parameter, modificationListener);
        }
    },
    Enumeration {
        @Override
        public IParameterSettingsModifier newModifier(final ISeriesParameter parameter,
                final Runnable modificationListener) {
            return new EnumerationParameterSettingsModifier(parameter, modificationListener);
        }
    };

    public abstract IParameterSettingsModifier newModifier(ISeriesParameter parameter, Runnable modificationListener);
}
