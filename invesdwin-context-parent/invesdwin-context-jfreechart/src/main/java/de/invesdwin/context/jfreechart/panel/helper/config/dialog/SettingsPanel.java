package de.invesdwin.context.jfreechart.panel.helper.config.dialog;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.annotation.concurrent.NotThreadSafe;
import javax.swing.JDialog;
import javax.swing.JPanel;

import de.invesdwin.context.jfreechart.panel.helper.config.PlotConfigurationHelper;
import de.invesdwin.context.jfreechart.panel.helper.config.dialog.expression.ExpressionSettingsPanel;
import de.invesdwin.context.jfreechart.panel.helper.config.dialog.indicator.IndicatorSettingsPanel;
import de.invesdwin.context.jfreechart.panel.helper.config.dialog.style.StyleSettingsPanel;
import de.invesdwin.context.jfreechart.panel.helper.legend.HighlightedLegendInfo;

@NotThreadSafe
public class SettingsPanel extends JPanel implements ISettingsPanelActions {

    private final StyleSettingsPanel styleSettings;
    private final IndicatorSettingsPanel parameterSettings;
    private final ExpressionSettingsPanel expressionSettings;
    private final SettingsPanelButtonsLayout buttons;

    public SettingsPanel(final PlotConfigurationHelper plotConfigurationHelper, final HighlightedLegendInfo highlighted,
            final JDialog dialog) {
        setLayout(new BorderLayout(0, 0));

        styleSettings = new StyleSettingsPanel(plotConfigurationHelper, highlighted, dialog);
        add(styleSettings, BorderLayout.CENTER);

        if (highlighted.getDataset().hasIndicatorSeriesArguments()) {
            expressionSettings = null;
            parameterSettings = new IndicatorSettingsPanel(plotConfigurationHelper, highlighted, dialog);
            add(parameterSettings, BorderLayout.WEST);
        } else if (highlighted.getDataset().hasExpressionSeriesArguments()) {
            parameterSettings = null;
            expressionSettings = new ExpressionSettingsPanel(plotConfigurationHelper, highlighted, dialog);
            add(expressionSettings, BorderLayout.NORTH);
        } else {
            parameterSettings = null;
            expressionSettings = null;
        }

        buttons = new SettingsPanelButtonsLayout();
        add(buttons, BorderLayout.SOUTH);

        buttons.btn_reset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                reset();
            }
        });
        buttons.btn_cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                cancel();
                dialog.dispose();
            }
        });
        buttons.btn_ok.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                ok();
                dialog.dispose();
            }
        });

        if (dialog != null) {
            dialog.getRootPane().setDefaultButton(buttons.btn_ok);
        }
    }

    @Override
    public void reset() {
        if (expressionSettings != null) {
            expressionSettings.reset();
        }
        if (parameterSettings != null) {
            parameterSettings.reset();
        }
        styleSettings.reset();
    }

    @Override
    public void cancel() {
        if (expressionSettings != null) {
            expressionSettings.cancel();
        }
        if (parameterSettings != null) {
            parameterSettings.cancel();
        }
        styleSettings.cancel();
    }

    @Override
    public void ok() {
        if (expressionSettings != null) {
            expressionSettings.ok();
        }
        if (parameterSettings != null) {
            parameterSettings.ok();
        }
        styleSettings.ok();
    }
}
