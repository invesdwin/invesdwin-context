package de.invesdwin.context.jfreechart.panel.helper.config.series;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.context.jfreechart.panel.helper.config.dialog.indicator.modifier.BooleanParameterSettingsModifier;
import de.invesdwin.context.jfreechart.panel.helper.config.dialog.indicator.modifier.DoubleParameterSettingsModifier;
import de.invesdwin.context.jfreechart.panel.helper.config.dialog.indicator.modifier.EnumerationParameterSettingsModifier;
import de.invesdwin.context.jfreechart.panel.helper.config.dialog.indicator.modifier.IParameterSettingsModifier;
import de.invesdwin.context.jfreechart.panel.helper.config.dialog.indicator.modifier.IntegerParameterSettingsModifier;
import de.invesdwin.context.jfreechart.panel.helper.config.series.indicator.IIndicatorSeriesParameter;

@Immutable
public enum SeriesParameterType {
    Integer {
        @Override
        public IParameterSettingsModifier newModifier(final IIndicatorSeriesParameter parameter,
                final Runnable modificationListener) {
            return new IntegerParameterSettingsModifier(parameter, modificationListener);
        }
    },
    Double {
        @Override
        public IParameterSettingsModifier newModifier(final IIndicatorSeriesParameter parameter,
                final Runnable modificationListener) {
            return new DoubleParameterSettingsModifier(parameter, modificationListener);
        }
    },
    Boolean {
        @Override
        public IParameterSettingsModifier newModifier(final IIndicatorSeriesParameter parameter,
                final Runnable modificationListener) {
            return new BooleanParameterSettingsModifier(parameter, modificationListener);
        }
    },
    Enumeration {
        @Override
        public IParameterSettingsModifier newModifier(final IIndicatorSeriesParameter parameter,
                final Runnable modificationListener) {
            return new EnumerationParameterSettingsModifier(parameter, modificationListener);
        }
    };

    public abstract IParameterSettingsModifier newModifier(IIndicatorSeriesParameter parameter, Runnable modificationListener);
}
