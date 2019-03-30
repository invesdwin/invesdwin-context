package de.invesdwin.context.jfreechart.panel.helper.config.indicator;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.util.Collection;

import javax.annotation.concurrent.NotThreadSafe;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import de.invesdwin.context.jfreechart.panel.helper.config.PlotConfigurationHelper;
import de.invesdwin.util.swing.listener.MouseListenerSupport;
import de.invesdwin.util.swing.listener.MouseMotionListenerSupport;

@NotThreadSafe
public class AddIndicatorSeriesPanel extends JPanel {

    private static final Cursor HAND_CURSOR = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
    private final JList<IIndicatorSeriesProvider> lst_indicators;

    public AddIndicatorSeriesPanel(final PlotConfigurationHelper plotConfigurationHelper) {
        setLayout(new BorderLayout());

        final JScrollPane scrollPane = new JScrollPane();
        add(scrollPane, BorderLayout.CENTER);

        final DefaultListModel<IIndicatorSeriesProvider> model = new DefaultListModel<>();
        final Collection<IIndicatorSeriesProvider> indicatorProviders = plotConfigurationHelper.getIndicatorProviders();
        for (final IIndicatorSeriesProvider indicatorProvider : indicatorProviders) {
            model.addElement(indicatorProvider);
        }

        lst_indicators = new JList<IIndicatorSeriesProvider>(model);
        scrollPane.setViewportView(lst_indicators);

        lst_indicators.setCursor(HAND_CURSOR);
        lst_indicators.addMouseMotionListener(new MouseMotionListenerSupport() {
            @Override
            public void mouseMoved(final MouseEvent e) {
                final int selectedIndex = lst_indicators.locationToIndex(e.getPoint());
                lst_indicators.setSelectedIndex(selectedIndex);
                final IIndicatorSeriesProvider selectedValue = lst_indicators.getSelectedValue();
                lst_indicators.setToolTipText(selectedValue.getDescription());
            }
        });
        lst_indicators.addMouseListener(new MouseListenerSupport() {
            @Override
            public void mouseReleased(final MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    final IIndicatorSeriesProvider selectedValue = lst_indicators.getSelectedValue();
                    selectedValue.newInstance(plotConfigurationHelper.getChartPanel(),
                            selectedValue.getDefaultValues());
                }
            }
        });
    }

}