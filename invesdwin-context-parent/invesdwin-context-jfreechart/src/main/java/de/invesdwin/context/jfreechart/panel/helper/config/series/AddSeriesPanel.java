package de.invesdwin.context.jfreechart.panel.helper.config.series;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.regex.Pattern;

import javax.annotation.concurrent.NotThreadSafe;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.DocumentEvent;

import de.invesdwin.context.jfreechart.panel.helper.config.PlotConfigurationHelper;
import de.invesdwin.context.jfreechart.panel.helper.icons.PlotIcons;
import de.invesdwin.util.lang.Strings;
import de.invesdwin.util.swing.icon.JIconTextField;
import de.invesdwin.util.swing.listener.DocumentListenerSupport;
import de.invesdwin.util.swing.listener.MouseListenerSupport;
import de.invesdwin.util.swing.listener.MouseMotionListenerSupport;

@NotThreadSafe
public class AddSeriesPanel extends JPanel {

    private static final Cursor HAND_CURSOR = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
    private final PlotConfigurationHelper plotConfigurationHelper;
    private final JList<ISeriesProvider> lst_series;
    private final JButton btn_close;
    private final JIconTextField tf_search;

    public AddSeriesPanel(final PlotConfigurationHelper plotConfigurationHelper, final AddSeriesDialog dialog) {
        this.plotConfigurationHelper = plotConfigurationHelper;
        setLayout(new BorderLayout());

        final DefaultListModel<ISeriesProvider> model = newListModel("");

        final JPanel pnl_close = new JPanel();
        add(pnl_close, BorderLayout.SOUTH);

        btn_close = new JButton("Close");
        pnl_close.add(btn_close);

        final JPanel pnl_search = new JPanel();
        add(pnl_search, BorderLayout.NORTH);

        tf_search = new JIconTextField();
        tf_search.setIcon(PlotIcons.SEARCH.newIcon(14));
        pnl_search.add(tf_search);
        tf_search.setColumns(20);

        final JPanel pnl_series = new JPanel();
        add(pnl_series, BorderLayout.CENTER);

        final JScrollPane scrl_series = new JScrollPane();
        pnl_series.add(scrl_series);

        lst_series = new JList<ISeriesProvider>(model);
        lst_series.setVisibleRowCount(16);
        scrl_series.setViewportView(lst_series);

        lst_series.setCursor(HAND_CURSOR);
        lst_series.addMouseMotionListener(new MouseMotionListenerSupport() {
            @Override
            public void mouseMoved(final MouseEvent e) {
                final int selectedIndex = lst_series.locationToIndex(e.getPoint());
                lst_series.setSelectedIndex(selectedIndex);
                final ISeriesProvider selectedValue = lst_series.getSelectedValue();
                lst_series.setToolTipText(selectedValue.getDescription());
            }
        });
        lst_series.addMouseListener(new MouseListenerSupport() {
            @Override
            public void mouseReleased(final MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    final ISeriesProvider selectedValue = lst_series.getSelectedValue();
                    selectedValue.newInstance(plotConfigurationHelper.getChartPanel(),
                            selectedValue.getDefaultValues());
                }
            }
        });

        btn_close.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                dialog.close();
            }
        });
        tf_search.getDocument().addDocumentListener(new DocumentListenerSupport() {

            @Override
            protected void update(final DocumentEvent e) {
                lst_series.setModel(newListModel(tf_search.getText()));
            }

        });
    }

    private DefaultListModel<ISeriesProvider> newListModel(final String search) {
        final DefaultListModel<ISeriesProvider> model = new DefaultListModel<>();
        final Collection<ISeriesProvider> seriesProviders = plotConfigurationHelper.getSeriesProviders();
        if (Strings.isBlank(search)) {
            for (final ISeriesProvider seriesProvider : seriesProviders) {
                model.addElement(seriesProvider);
            }
        } else {
            final String searchString = search.trim();
            final Pattern searchPattern = Pattern.compile(searchString);
            for (final ISeriesProvider seriesProvider : seriesProviders) {
                if (matches(seriesProvider, searchString, searchPattern)) {
                    model.addElement(seriesProvider);
                }
            }
        }
        return model;
    }

    private boolean matches(final ISeriesProvider seriesProvider, final String searchString,
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