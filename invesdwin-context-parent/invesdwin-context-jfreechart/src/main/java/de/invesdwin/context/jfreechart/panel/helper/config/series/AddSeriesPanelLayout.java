package de.invesdwin.context.jfreechart.panel.helper.config.series;

import java.awt.BorderLayout;

import javax.annotation.concurrent.NotThreadSafe;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import de.invesdwin.context.jfreechart.panel.helper.icons.PlotIcons;
import de.invesdwin.util.swing.icon.JIconTextField;

@NotThreadSafe
public class AddSeriesPanelLayout extends JPanel {

    //CHECKSTYLE:OFF
    public final JButton btn_close;
    public final JIconTextField tf_search;
    public final JTable tbl_series;
    //CHECKSTYLE:ON

    public AddSeriesPanelLayout() {
        setLayout(new BorderLayout());

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
        pnl_series.setBorder(new EmptyBorder(0, 10, 0, 10));
        add(pnl_series, BorderLayout.CENTER);
        pnl_series.setLayout(new BorderLayout(0, 0));

        final JScrollPane scrl_series = new JScrollPane();
        pnl_series.add(scrl_series);

        tbl_series = new JTable(new DefaultTableModel(
                new Object[][] { { "1", "2" }, { "3", "4" }, { "5", "6" }, { "7", "8" }, { "9", "10" }, { "11", "12" },
                        { "13", "14" }, { "15", "16" }, { "17", "18" }, { "19", "20" }, },
                new String[] { "New column", "New column" }));
        tbl_series.setShowVerticalLines(false);
        tbl_series.setShowHorizontalLines(false);
        tbl_series.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tbl_series.setShowGrid(false);
        tbl_series.setAutoCreateRowSorter(true);
        scrl_series.setViewportView(tbl_series);
    }
}