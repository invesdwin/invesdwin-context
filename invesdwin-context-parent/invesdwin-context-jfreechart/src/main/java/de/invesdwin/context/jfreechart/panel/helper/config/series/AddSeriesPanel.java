package de.invesdwin.context.jfreechart.panel.helper.config.series;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.Collection;

import javax.annotation.concurrent.NotThreadSafe;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import de.invesdwin.context.jfreechart.panel.helper.config.PlotConfigurationHelper;
import de.invesdwin.util.swing.listener.MouseListenerSupport;
import de.invesdwin.util.swing.listener.MouseMotionListenerSupport;

@NotThreadSafe
public class AddSeriesPanel extends JPanel {

    private static final Cursor HAND_CURSOR = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
    private final JList<ISeriesProvider> lst_indicators;
    private final JButton btn_close;

    public AddSeriesPanel(final PlotConfigurationHelper plotConfigurationHelper,
            final AddSeriesDialog dialog) {
        setLayout(new BorderLayout());

        final JScrollPane scrollPane = new JScrollPane();
        add(scrollPane, BorderLayout.CENTER);

        final DefaultListModel<ISeriesProvider> model = new DefaultListModel<>();
        final Collection<ISeriesProvider> indicatorProviders = plotConfigurationHelper.getSeriesProviders();
        for (final ISeriesProvider indicatorProvider : indicatorProviders) {
            model.addElement(indicatorProvider);
        }

        lst_indicators = new JList<ISeriesProvider>(model);
        scrollPane.setViewportView(lst_indicators);

        lst_indicators.setCursor(HAND_CURSOR);

        final JPanel panel = new JPanel();
        add(panel, BorderLayout.SOUTH);

        btn_close = new JButton("Close");
        btn_close.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                dialog.close();
            }
        });
        panel.add(btn_close);
        lst_indicators.addMouseMotionListener(new MouseMotionListenerSupport() {
            @Override
            public void mouseMoved(final MouseEvent e) {
                final int selectedIndex = lst_indicators.locationToIndex(e.getPoint());
                lst_indicators.setSelectedIndex(selectedIndex);
                final ISeriesProvider selectedValue = lst_indicators.getSelectedValue();
                lst_indicators.setToolTipText(selectedValue.getDescription());
            }
        });
        lst_indicators.addMouseListener(new MouseListenerSupport() {
            @Override
            public void mouseReleased(final MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    final ISeriesProvider selectedValue = lst_indicators.getSelectedValue();
                    selectedValue.newInstance(plotConfigurationHelper.getChartPanel(),
                            selectedValue.getDefaultValues());
                }
            }
        });
    }
}