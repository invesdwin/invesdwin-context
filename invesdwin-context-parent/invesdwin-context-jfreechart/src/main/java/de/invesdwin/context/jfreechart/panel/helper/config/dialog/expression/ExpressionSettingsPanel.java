package de.invesdwin.context.jfreechart.panel.helper.config.dialog.expression;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.annotation.concurrent.NotThreadSafe;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;

import org.springframework.web.util.HtmlUtils;

import de.invesdwin.context.jfreechart.panel.helper.config.PlotConfigurationHelper;
import de.invesdwin.context.jfreechart.panel.helper.config.dialog.ISettingsPanelActions;
import de.invesdwin.context.jfreechart.panel.helper.config.series.AddSeriesPanel;
import de.invesdwin.context.jfreechart.panel.helper.config.series.expression.IExpressionSeriesProvider;
import de.invesdwin.context.jfreechart.panel.helper.legend.HighlightedLegendInfo;
import de.invesdwin.context.jfreechart.plot.dataset.IPlotSourceDataset;
import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.error.Throwables;
import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.swing.Dialogs;
import de.invesdwin.util.swing.listener.DocumentListenerSupport;

@NotThreadSafe
public class ExpressionSettingsPanel extends JPanel implements ISettingsPanelActions {

    private static final Color COLOR_EXPRESSION_PENDING_INVALID = Color.RED;

    private static final Color COLOR_EXPRESSION_PENDING_VALID = Color.GREEN;

    private static final org.slf4j.ext.XLogger LOG = org.slf4j.ext.XLoggerFactory.getXLogger(AddSeriesPanel.class);

    private final PlotConfigurationHelper plotConfigurationHelper;
    private final IPlotSourceDataset dataset;
    private final String expressionArgumentsBefore;
    private final HighlightedLegendInfo highlighted;

    private final ExpressionSettingsPanelLayout layout;

    public ExpressionSettingsPanel(final PlotConfigurationHelper plotConfigurationHelper,
            final HighlightedLegendInfo highlighted, final JDialog dialog) {
        Assertions.checkFalse(highlighted.isPriceSeries());
        Assertions.checkNotNull(highlighted.getDataset().getExpressionSeriesProvider());

        setLayout(new BorderLayout());
        final TitledBorder titleBorder = new TitledBorder(null, "Expression", TitledBorder.LEADING, TitledBorder.TOP,
                null, null);
        final EmptyBorder marginBorder = new EmptyBorder(10, 10, 10, 10);
        setBorder(new CompoundBorder(new CompoundBorder(marginBorder, titleBorder), marginBorder));

        this.plotConfigurationHelper = plotConfigurationHelper;
        this.highlighted = highlighted;
        this.dataset = highlighted.getDataset();
        this.expressionArgumentsBefore = dataset.getExpressionSeriesArguments();
        this.layout = new ExpressionSettingsPanelLayout();
        add(layout, BorderLayout.CENTER);
        setExpressionValue(expressionArgumentsBefore);
        layout.ta_expression.getDocument().addDocumentListener(new DocumentListenerSupport() {
            @Override
            protected void update(final DocumentEvent e) {
                final String fromExpression = dataset.getExpressionSeriesArguments();
                final String toExpression = getExpressionValue();
                if (hasChanges(fromExpression, toExpression)) {
                    try {
                        dataset.getExpressionSeriesProvider().parseExpression(toExpression);
                        layout.ta_expression.setBorder(BorderFactory.createLineBorder(COLOR_EXPRESSION_PENDING_VALID));
                        layout.ta_expression.setToolTipText(null);
                    } catch (final Throwable t) {
                        layout.ta_expression
                                .setBorder(BorderFactory.createLineBorder(COLOR_EXPRESSION_PENDING_INVALID));
                        layout.ta_expression
                                .setToolTipText(newInvalidExpressionMessage(fromExpression, toExpression, t));
                    }
                } else {
                    layout.ta_expression.setBorder(null);
                }
            }
        });
        layout.btn_apply.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                ok();
            }
        });
    }

    @Override
    public void reset() {
        final String expressionArgumentsInitial = plotConfigurationHelper.getSeriesInitialSettings(highlighted)
                .getExpressionSeriesArguments();
        setExpressionValue(expressionArgumentsInitial);
        if (hasChanges(expressionArgumentsInitial, dataset.getExpressionSeriesArguments())) {
            apply(expressionArgumentsInitial);
        }
    }

    private void setExpressionValue(final String value) {
        layout.ta_expression.setText(value);
        layout.ta_expression.setBorder(null);
    }

    @Override
    public void cancel() {
        if (hasChanges(expressionArgumentsBefore, dataset.getExpressionSeriesArguments())) {
            apply(expressionArgumentsBefore);
        }
    }

    @Override
    public void ok() {
        final String newExpression = getExpressionValue();
        if (hasChanges(dataset.getExpressionSeriesArguments(), newExpression)) {
            apply(newExpression);
        } else {
            layout.ta_expression.setBorder(null);
        }
    }

    private String getExpressionValue() {
        return layout.ta_expression.getText();
    }

    public void apply(final String toExpression) {
        final IExpressionSeriesProvider seriesProvider = dataset.getExpressionSeriesProvider();
        try {
            seriesProvider.modifyDataset(plotConfigurationHelper.getChartPanel(), dataset, toExpression);
            dataset.setExpressionSeriesArguments(toExpression);
            dataset.setSeriesTitle(toExpression);
            layout.ta_expression.setBorder(null);
        } catch (final Throwable t) {
            final String fromExpression = dataset.getSeriesTitle();
            LOG.warn("Error modifying series expression from [" + fromExpression + "] to [" + toExpression + "]:\n"
                    + Throwables.getFullStackTrace(t));
            layout.ta_expression.setBorder(BorderFactory.createLineBorder(COLOR_EXPRESSION_PENDING_INVALID));
            Dialogs.showMessageDialog(this, newInvalidExpressionMessage(fromExpression, toExpression, t),
                    "Invalid Expression", Dialogs.ERROR_MESSAGE);
        }
    }

    private String newInvalidExpressionMessage(final String fromExpression, final String toExpression,
            final Throwable t) {
        return "<html><b>Valid Before:</b><br><pre>  " + fromExpression + "</pre><b>Invalid After:</b><br><pre>  "
                + toExpression + "</pre><br><b>Error:</b><br><pre>  "
                + HtmlUtils.htmlEscape(Throwables.concatMessagesShort(t).replace("\n", "\n  ")) + "</pre>";
    }

    private boolean hasChanges(final String arguments1, final String arguments2) {
        return !Objects.equals(arguments1, arguments2);
    }

}
