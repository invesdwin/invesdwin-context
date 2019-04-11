package de.invesdwin.context.jfreechart.panel.helper.config.dialog.expression;

import java.awt.FlowLayout;

import javax.annotation.concurrent.NotThreadSafe;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.springframework.web.util.HtmlUtils;

import de.invesdwin.context.jfreechart.panel.helper.config.PlotConfigurationHelper;
import de.invesdwin.context.jfreechart.panel.helper.config.dialog.ISettingsPanelActions;
import de.invesdwin.context.jfreechart.panel.helper.config.dialog.indicator.modifier.IParameterSettingsModifier;
import de.invesdwin.context.jfreechart.panel.helper.config.series.AddSeriesPanel;
import de.invesdwin.context.jfreechart.panel.helper.config.series.expression.IExpressionSeriesProvider;
import de.invesdwin.context.jfreechart.panel.helper.config.series.indicator.IIndicatorSeriesParameter;
import de.invesdwin.context.jfreechart.panel.helper.legend.HighlightedLegendInfo;
import de.invesdwin.context.jfreechart.plot.dataset.IPlotSourceDataset;
import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.error.Throwables;
import de.invesdwin.util.math.expression.IExpression;
import de.invesdwin.util.swing.Dialogs;

@NotThreadSafe
public class ExpressionSettingsPanel extends JPanel implements ISettingsPanelActions {

    private static final org.slf4j.ext.XLogger LOG = org.slf4j.ext.XLoggerFactory.getXLogger(AddSeriesPanel.class);

    private final PlotConfigurationHelper plotConfigurationHelper;
    private final IPlotSourceDataset dataset;
    private final IExpression[] seriesArgumentsBefore;
    private final HighlightedLegendInfo highlighted;

    private final ExpressionSettingsPanelLayout layout;
    private boolean notifyModification = false;

    public ExpressionSettingsPanel(final PlotConfigurationHelper plotConfigurationHelper,
            final HighlightedLegendInfo highlighted, final JDialog dialog) {
        Assertions.checkFalse(highlighted.isPriceSeries());
        Assertions.checkNotNull(highlighted.getDataset().getIndicatorSeriesProvider());

        final FlowLayout flowLayout = (FlowLayout) getLayout();
        flowLayout.setVgap(0);
        final TitledBorder titleBorder = new TitledBorder(null, "Expression", TitledBorder.LEADING, TitledBorder.TOP,
                null, null);
        final EmptyBorder marginBorder = new EmptyBorder(10, 10, 10, 10);
        setBorder(new CompoundBorder(new CompoundBorder(marginBorder, titleBorder), marginBorder));

        this.plotConfigurationHelper = plotConfigurationHelper;
        this.highlighted = highlighted;
        this.dataset = highlighted.getDataset();
        final IIndicatorSeriesParameter[] parameters = dataset.getIndicatorSeriesProvider().getParameters();
        final IParameterSettingsModifier[] modifiers = new IParameterSettingsModifier[parameters.length];
        final IExpression[] args = dataset.getIndicatorSeriesArguments();
        final Runnable modificationListener = new Runnable() {
            @Override
            public void run() {
                if (notifyModification) {
                    ok();
                }
            }
        };
        for (int i = 0; i < parameters.length; i++) {
            final IIndicatorSeriesParameter parameter = parameters[i];
            final IParameterSettingsModifier modifier = parameter.newModifier(modificationListener);
            modifier.setValue(args[i]);
            modifiers[i] = modifier;
        }
        this.seriesArgumentsBefore = args;
        this.layout = new ExpressionSettingsPanelLayout(modifiers);
        add(layout);
        notifyModification = true;
    }

    @Override
    public void reset() {
        final IExpression[] seriesArgumentsInitial = plotConfigurationHelper.getSeriesInitialSettings(highlighted)
                .getSeriesArguments();
        setModifierValues(seriesArgumentsInitial);
        if (hasChanges(seriesArgumentsInitial, dataset.getIndicatorSeriesArguments())) {
            apply(seriesArgumentsInitial);
        }
    }

    @Override
    public void cancel() {
        if (hasChanges(seriesArgumentsBefore, dataset.getIndicatorSeriesArguments())) {
            apply(seriesArgumentsBefore);
        }
    }

    @Override
    public void ok() {
        final IExpression[] newSeriesArguments = newSeriesArguments();
        if (hasChanges(dataset.getIndicatorSeriesArguments(), newSeriesArguments)) {
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

    public void apply(final String expression) {
        final IExpressionSeriesProvider seriesProvider = dataset.getIndicatorSeriesProvider();
        final String toExpression = seriesProvider.getExpressionString(arguments);
        try {
            seriesProvider.modifyDataset(plotConfigurationHelper.getChartPanel(), dataset, arguments);
            dataset.setIndicatorSeriesArguments(arguments);
            dataset.setSeriesTitle(toExpression);
        } catch (final Throwable t) {
            final String fromExpression = dataset.getSeriesTitle();
            LOG.warn("Error modifying series [" + seriesProvider.getName() + "] expression from [" + fromExpression
                    + "] to [" + toExpression + "]:\n" + Throwables.getFullStackTrace(t));
            Dialogs.showMessageDialog(this,
                    "<html><b>Name:</b><br><pre>  " + seriesProvider.getName() + "</pre><b>Expression:</b><br><pre>  "
                            + fromExpression + "</pre><b>Invalid:</b><br><pre>  " + toExpression
                            + "</pre><br><b>Error:</b><br><pre>  "
                            + HtmlUtils.htmlEscape(Throwables.concatMessagesShort(t).replace("\n", "\n  ")) + "</pre>",
                    "Invalid Inputs", Dialogs.ERROR_MESSAGE);

            final IExpression[] seriesArgumentsValid = dataset.getIndicatorSeriesArguments();
            setModifierValues(seriesArgumentsValid);
        }
    }

    private void setModifierValues(final IExpression[] values) {
        notifyModification = false;
        try {
            for (int i = 0; i < values.length; i++) {
                layout.modifiers[i].setValue(values[i]);
            }
        } finally {
            notifyModification = true;
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
