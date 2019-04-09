package de.invesdwin.context.jfreechart.panel.helper.config.series;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.context.jfreechart.panel.helper.config.dialog.parameter.ParameterSettingsPanel;
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
                final ParameterSettingsPanel panel) {
            return new IntegerParameterSettingsModifier(parameter, panel);
        }
    },
    Double {
        @Override
        public IParameterSettingsModifier newModifier(final ISeriesParameter parameter,
                final ParameterSettingsPanel panel) {
            return new DoubleParameterSettingsModifier(parameter, panel);
        }
    },
    Boolean {
        @Override
        public IParameterSettingsModifier newModifier(final ISeriesParameter parameter,
                final ParameterSettingsPanel panel) {
            return new BooleanParameterSettingsModifier(parameter, panel);
        }
    },
    Enumeration {
        @Override
        public IParameterSettingsModifier newModifier(final ISeriesParameter parameter,
                final ParameterSettingsPanel panel) {
            return new EnumerationParameterSettingsModifier(parameter, panel);
        }
    };

    public abstract IParameterSettingsModifier newModifier(ISeriesParameter parameter, ParameterSettingsPanel panel);
}
