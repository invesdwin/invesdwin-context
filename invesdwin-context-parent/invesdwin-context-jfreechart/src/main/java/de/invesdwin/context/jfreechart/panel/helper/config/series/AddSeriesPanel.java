package de.invesdwin.context.jfreechart.panel.helper.config.series;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.regex.Pattern;

import javax.annotation.concurrent.NotThreadSafe;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;

import org.springframework.web.util.HtmlUtils;

import de.invesdwin.context.jfreechart.panel.helper.config.PlotConfigurationHelper;
import de.invesdwin.context.jfreechart.panel.helper.config.series.indicator.IIndicatorSeriesProvider;
import de.invesdwin.context.jfreechart.plot.dataset.IPlotSourceDataset;
import de.invesdwin.util.error.Throwables;
import de.invesdwin.util.lang.Strings;
import de.invesdwin.util.math.expression.IExpression;
import de.invesdwin.util.swing.Dialogs;
import de.invesdwin.util.swing.listener.DocumentListenerSupport;
import de.invesdwin.util.swing.listener.MouseListenerSupport;
import de.invesdwin.util.swing.listener.MouseMotionListenerSupport;

@NotThreadSafe
public class AddSeriesPanel extends JPanel {

    private static final org.slf4j.ext.XLogger LOG = org.slf4j.ext.XLoggerFactory.getXLogger(AddSeriesPanel.class);
    private static final Cursor HAND_CURSOR = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
    private final PlotConfigurationHelper plotConfigurationHelper;

    private final AddSeriesPanelLayout panel;

    public AddSeriesPanel(final PlotConfigurationHelper plotConfigurationHelper, final AddSeriesDialog dialog) {
        this.plotConfigurationHelper = plotConfigurationHelper;
        setLayout(new BorderLayout());

        panel = new AddSeriesPanelLayout();
        add(panel, BorderLayout.CENTER);

        panel.tbl_series.setModel(newTableModel(""));

        panel.tbl_series.setCursor(HAND_CURSOR);
        panel.tbl_series.addMouseMotionListener(new MouseMotionListenerSupport() {
            @Override
            public void mouseMoved(final MouseEvent e) {
                final int selectedRow = panel.tbl_series.rowAtPoint(e.getPoint());
                panel.tbl_series.setRowSelectionInterval(selectedRow, selectedRow);
                final String selectedName = (String) panel.tbl_series.getModel().getValueAt(selectedRow, 0);
                final IIndicatorSeriesProvider selectedValue = plotConfigurationHelper.getIndicatorSeriesProvider(selectedName);
                panel.tbl_series.setToolTipText(selectedValue.getDescription());
            }
        });
        panel.tbl_series.addMouseListener(new MouseListenerSupport() {
            @Override
            public void mouseReleased(final MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    final int selectedRow = panel.tbl_series.getSelectedRow();
                    final String selectedName = (String) panel.tbl_series.getModel().getValueAt(selectedRow, 0);
                    final IIndicatorSeriesProvider selectedValue = plotConfigurationHelper
                            .getIndicatorSeriesProvider(selectedName);
                    final IExpression[] args = selectedValue.getDefaultValues();
                    final String expressionString = selectedValue.getExpressionString(args);
                    try {
                        final IPlotSourceDataset dataset = selectedValue
                                .newInstance(plotConfigurationHelper.getChartPanel(), args);
                        dataset.setIndicatorSeriesProvider(selectedValue);
                        dataset.setIndicatorSeriesArguments(args);
                        dataset.setSeriesTitle(expressionString);
                    } catch (final Throwable t) {
                        LOG.warn("Error adding series [" + selectedValue.getName() + "] with expression ["
                                + expressionString + "]\n" + Throwables.getFullStackTrace(t));

                        Dialogs.showMessageDialog(panel,
                                "<html><b>Name:</b><br><pre>  " + selectedValue.getName()
                                        + "</pre><b>Expression:</b><br><pre>  " + expressionString
                                        + "</pre><br><b>Error:</b><br><pre>  "
                                        + HtmlUtils.htmlEscape(Throwables.concatMessagesShort(t).replace("\n", "\n  "))
                                        + "</pre>",
                                "Error", Dialogs.ERROR_MESSAGE);
                    }
                }
            }
        });

        panel.btn_close.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                dialog.close();
            }
        });
        panel.tf_search.getDocument().addDocumentListener(new DocumentListenerSupport() {
            @Override
            protected void update(final DocumentEvent e) {
                panel.tbl_series.setModel(newTableModel(panel.tf_search.getText()));
            }
        });

        if (dialog != null) {
            dialog.getRootPane().setDefaultButton(panel.btn_close);
        }
    }

    private DefaultTableModel newTableModel(final String search) {
        final DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Name");
        model.addColumn("Expression");
        final Collection<IIndicatorSeriesProvider> seriesProviders = plotConfigurationHelper.getIndicatorSeriesProviders();
        if (Strings.isBlank(search)) {
            for (final IIndicatorSeriesProvider seriesProvider : seriesProviders) {
                model.addRow(new Object[] { seriesProvider.getName(),
                        seriesProvider.getExpressionString(seriesProvider.getDefaultValues()) });
            }
        } else {
            final String searchString = search.trim();
            final Pattern searchPattern = Pattern.compile(searchString);
            for (final IIndicatorSeriesProvider seriesProvider : seriesProviders) {
                if (matches(seriesProvider, searchString, searchPattern)) {
                    model.addRow(new Object[] { seriesProvider.getName(),
                            seriesProvider.getExpressionString(seriesProvider.getDefaultValues()) });
                }
            }
        }
        return model;
    }

    private boolean matches(final IIndicatorSeriesProvider seriesProvider, final String searchString,
            final Pattern searchPattern) {
        if (matches(searchString, searchPattern, seriesProvider.getName())) {
            return true;
        } else if (matches(searchString, searchPattern, seriesProvider.getDescription())) {
            return true;
        } else if (matches(searchString, searchPattern, seriesProvider.getExpressionName())) {
            return true;
        }
        return false;
    }

    private boolean matches(final String searchString, final Pattern searchPattern, final String value) {
        return Strings.isNotBlank(value)
                && (Strings.containsIgnoreCase(value, searchString) || searchPattern.matcher(value).matches());
    }
}