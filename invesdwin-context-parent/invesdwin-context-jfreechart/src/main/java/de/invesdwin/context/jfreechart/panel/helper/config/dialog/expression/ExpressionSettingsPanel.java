package de.invesdwin.context.jfreechart.panel.helper.config.dialog.expression;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.annotation.concurrent.NotThreadSafe;
import javax.swing.Icon;
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
import de.invesdwin.util.lang.Strings;
import de.invesdwin.util.math.expression.IExpression;
import de.invesdwin.util.swing.Dialogs;
import de.invesdwin.util.swing.listener.DocumentListenerSupport;

@NotThreadSafe
public class ExpressionSettingsPanel extends JPanel implements ISettingsPanelActions {

    public static final Icon ICON_EXPRESSION = AddSeriesPanel.ICON_EXPRESSION;
    public static final Icon ICON_EXPRESSION_PENDING_INVALID = AddSeriesPanel.ICON_EXPRESSION_PENDING_INVALID;
    public static final Icon ICON_EXPRESSION_PENDING_VALID = AddSeriesPanel.ICON_EXPRESSION_PENDING_VALID;

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
        setBorder(new CompoundBorder(
                new TitledBorder(null, "Expression", TitledBorder.LEADING, TitledBorder.TOP, null, null),
                new EmptyBorder(0, 5, 5, 5)));

        this.plotConfigurationHelper = plotConfigurationHelper;
        this.highlighted = highlighted;
        this.dataset = highlighted.getDataset();
        this.expressionArgumentsBefore = dataset.getExpressionSeriesArguments();
        this.layout = new ExpressionSettingsPanelLayout();
        add(layout, BorderLayout.CENTER);
        setExpressionValue(expressionArgumentsBefore);
        layout.tf_expression.getDocument().addDocumentListener(new DocumentListenerSupport() {
            @Override
            protected void update(final DocumentEvent e) {
                final String fromExpression = dataset.getExpressionSeriesArguments();
                final String toExpression = getExpressionValue();
                validateExpression(fromExpression, toExpression);
            }

        });
        layout.btn_applyExpression.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                ok();
            }
        });
        //set tooltip but use default icon
        validateExpression(null, getExpressionValue());
        layout.tf_expression.setIcon(ICON_EXPRESSION);
    }

    private void validateExpression(final String fromExpression, final String toExpression) {
        if (hasChanges(fromExpression, toExpression) && Strings.isNotBlank(toExpression)) {
            try {
                final IExpression parsedExpression = dataset.getExpressionSeriesProvider()
                        .parseExpression(toExpression);
                layout.tf_expression.setIcon(ICON_EXPRESSION_PENDING_VALID);
                layout.tf_expression.setToolTipText("<html><b>Validated:</b><br><pre>  "
                        + HtmlUtils.htmlEscape(parsedExpression.toString().replace("\n", "\n  ")) + "</pre>");
            } catch (final Throwable t) {
                layout.tf_expression.setIcon(ICON_EXPRESSION_PENDING_INVALID);
                layout.tf_expression.setToolTipText("<html><b>Error:</b><br><pre>  "
                        + HtmlUtils.htmlEscape(Throwables.concatMessagesShort(t).replace("\n", "\n  ")) + "</pre>");
            }
        } else {
            layout.tf_expression.setIcon(ICON_EXPRESSION);
            layout.tf_expression.setToolTipText(null);
        }
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
        layout.tf_expression.setText(value);
        layout.tf_expression.setIcon(ICON_EXPRESSION);
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
            layout.tf_expression.setIcon(ICON_EXPRESSION);
        }
    }

    private String getExpressionValue() {
        return layout.tf_expression.getText();
    }

    public void apply(final String toExpression) {
        final IExpressionSeriesProvider seriesProvider = dataset.getExpressionSeriesProvider();
        try {
            seriesProvider.modifyDataset(plotConfigurationHelper.getChartPanel(), dataset, toExpression);
            dataset.setExpressionSeriesArguments(toExpression);
            dataset.setSeriesTitle(toExpression);
            layout.tf_expression.setIcon(ICON_EXPRESSION);
        } catch (final Throwable t) {
            final String fromExpression = dataset.getSeriesTitle();
            LOG.warn("Error modifying series expression from [" + fromExpression + "] to [" + toExpression + "]:\n"
                    + Throwables.getFullStackTrace(t));
            Dialogs.showMessageDialog(this,
                    "<html><b>Valid Before:</b><br><pre>  " + fromExpression + "</pre><b>Invalid After:</b><br><pre>  "
                            + toExpression + "</pre><br><b>Error:</b><br><pre>  "
                            + HtmlUtils.htmlEscape(Throwables.concatMessagesShort(t).replace("\n", "\n  ")) + "</pre>",
                    "Invalid Expression", Dialogs.ERROR_MESSAGE);
        }
    }

    private boolean hasChanges(final String arguments1, final String arguments2) {
        return !Objects.equals(arguments1, arguments2);
    }

}
