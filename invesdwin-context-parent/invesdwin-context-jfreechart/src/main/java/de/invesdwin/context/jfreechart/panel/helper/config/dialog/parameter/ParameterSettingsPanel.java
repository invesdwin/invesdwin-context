package de.invesdwin.context.jfreechart.panel.helper.config.dialog.parameter;

import javax.annotation.concurrent.NotThreadSafe;
import javax.swing.JDialog;
import javax.swing.JPanel;

import de.invesdwin.context.jfreechart.panel.helper.config.PlotConfigurationHelper;
import de.invesdwin.context.jfreechart.panel.helper.config.dialog.ISettingsPanelActions;
import de.invesdwin.context.jfreechart.panel.helper.config.dialog.parameter.modifier.IParameterSettingsModifier;
import de.invesdwin.context.jfreechart.panel.helper.config.series.AddSeriesPanel;
import de.invesdwin.context.jfreechart.panel.helper.config.series.ISeriesParameter;
import de.invesdwin.context.jfreechart.panel.helper.config.series.ISeriesProvider;
import de.invesdwin.context.jfreechart.panel.helper.legend.HighlightedLegendInfo;
import de.invesdwin.context.jfreechart.plot.dataset.IPlotSourceDataset;
import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.error.Throwables;
import de.invesdwin.util.math.expression.IExpression;
import de.invesdwin.util.swing.Dialogs;

@NotThreadSafe
public class ParameterSettingsPanel extends JPanel implements ISettingsPanelActions {

    private static final org.slf4j.ext.XLogger LOG = org.slf4j.ext.XLoggerFactory.getXLogger(AddSeriesPanel.class);

    private final PlotConfigurationHelper plotConfigurationHelper;
    private final IPlotSourceDataset dataset;
    private final ISeriesProvider seriesProvider;
    private final IExpression[] seriesArgumentsBefore;
    private final HighlightedLegendInfo highlighted;

    private final ParameterSettingsPanelLayout layout;

    public ParameterSettingsPanel(final PlotConfigurationHelper plotConfigurationHelper,
            final HighlightedLegendInfo highlighted, final JDialog dialog) {
        Assertions.checkFalse(highlighted.isPriceSeries());
        this.plotConfigurationHelper = plotConfigurationHelper;
        this.highlighted = highlighted;
        this.dataset = highlighted.getDataset();
        final ISeriesParameter[] parameters = dataset.getSeriesProvider().getParameters();
        final IParameterSettingsModifier[] modifiers = new IParameterSettingsModifier[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            final ISeriesParameter parameter = parameters[i];
            modifiers[i] = parameter.newModifier(this);
        }
        this.seriesProvider = dataset.getSeriesProvider();
        this.seriesArgumentsBefore = dataset.getSeriesArguments();
        this.layout = new ParameterSettingsPanelLayout(modifiers);
    }

    @Override
    public void reset() {
        final IExpression[] seriesArgumentsInitial = plotConfigurationHelper.getSeriesInitialSettings(highlighted)
                .getSeriesArguments();

        for (int i = 0; i < seriesArgumentsInitial.length; i++) {
            layout.modifiers[i].setValue(seriesArgumentsInitial[i]);
        }
        if (hasChanges(seriesArgumentsInitial, dataset.getSeriesArguments())) {
            apply(seriesArgumentsInitial);
        }
    }

    @Override
    public void cancel() {
        if (hasChanges(seriesArgumentsBefore, dataset.getSeriesArguments())) {
            apply(seriesArgumentsBefore);
        }
    }

    @Override
    public void ok() {
        final IExpression[] newSeriesArguments = newSeriesArguments();
        if (hasChanges(seriesArgumentsBefore, newSeriesArguments)) {
            apply(newSeriesArguments);
        }
    }

    public IExpression[] newSeriesArguments() {
        final IExpression[] args = new IExpression[seriesArgumentsBefore.length];
        for (int i = 0; i < args.length; i++) {
            args[i] = layout.modifiers[i].getValue();
        }
        return args;
    }

    public void apply(final IExpression[] arguments) {
        try {
            seriesProvider.modifyDataset(plotConfigurationHelper.getChartPanel(), dataset, arguments);
            dataset.setSeriesArguments(arguments);
        } catch (final Throwable t) {
            final String seriesTitle = dataset.getSeriesTitle();
            LOG.warn("Error Modifying Series: " + seriesTitle + "\n" + Throwables.getFullStackTrace(t));
            Dialogs.showMessageDialog(this, Throwables.concatMessagesShort(t), "Error Modifying Series: " + seriesTitle,
                    Dialogs.ERROR_MESSAGE);

            final IExpression[] seriesArgumentsValid = dataset.getSeriesArguments();
            for (int i = 0; i < seriesArgumentsValid.length; i++) {
                layout.modifiers[i].setValue(seriesArgumentsValid[i]);
            }
        }
    }

    private boolean hasChanges(final IExpression[] arguments1, final IExpression[] arguments2) {
        for (int i = 0; i < arguments1.length; i++) {
            if (arguments1[i].evaluateDouble() != arguments2[i].evaluateDouble()) {
                return true;
            }
        }
        return false;
    }

}
