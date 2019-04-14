package de.invesdwin.context.jfreechart.panel.helper.config.series;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.annotation.concurrent.NotThreadSafe;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import de.invesdwin.context.jfreechart.panel.helper.icons.PlotIcons;
import de.invesdwin.util.swing.icon.JIconTextField;

@NotThreadSafe
public class AddSeriesPanelLayout extends JPanel {

    //CHECKSTYLE:OFF
    public final JPanel pnl_expression;
    public final JIconTextField tf_expression;
    public final JButton btn_addExpression;
    public final JIconTextField tf_search;
    public final JPanel pnl_indicator;
    public final JTable tbl_indicator;
    public final JButton btn_close;
    //CHECKSTYLE:ON

    public AddSeriesPanelLayout() {
        setLayout(new BorderLayout());

        pnl_expression = new JPanel();
        pnl_expression.setBorder(new CompoundBorder(
                new TitledBorder(null, "Expression", TitledBorder.LEADING, TitledBorder.TOP, null, null),
                new EmptyBorder(0, 5, 5, 5)));
        add(pnl_expression, BorderLayout.NORTH);
        pnl_expression.setLayout(new BorderLayout(5, 5));

        btn_addExpression = new JButton("Add");
        pnl_expression.add(btn_addExpression, BorderLayout.EAST);

        tf_expression = new JIconTextField();
        tf_expression.setMinimumSize(new Dimension(50, 24));
        tf_expression.setPreferredSize(new Dimension(50, 24));
        tf_expression.setIcon(PlotIcons.EXPRESSION.newIcon(14));
        pnl_expression.add(tf_expression, BorderLayout.CENTER);

        pnl_indicator = new JPanel();
        pnl_indicator.setLayout(new BorderLayout(5, 5));
        pnl_indicator.setBorder(new CompoundBorder(
                new TitledBorder(null, "Indicator", TitledBorder.LEADING, TitledBorder.TOP, null, null),
                new EmptyBorder(0, 5, 5, 5)));
        add(pnl_indicator, BorderLayout.CENTER);

        tf_search = new JIconTextField();
        tf_search.setIcon(PlotIcons.SEARCH.newIcon(14));
        tf_search.setMinimumSize(new Dimension(50, 24));
        tf_search.setPreferredSize(new Dimension(50, 24));
        pnl_indicator.add(tf_search, BorderLayout.NORTH);

        final JScrollPane scrl_indicator = new JScrollPane();
        pnl_indicator.add(scrl_indicator, BorderLayout.CENTER);

        tbl_indicator = new JTable(new DefaultTableModel(
                new Object[][] { { "1", "2" }, { "3", "4" }, { "5", "6" }, { "7", "8" }, { "9", "10" }, { "11", "12" },
                        { "13", "14" }, { "15", "16" }, { "17", "18" }, { "19", "20" }, },
                new String[] { "New column", "New column" }));
        tbl_indicator.setEnabled(false);
        tbl_indicator.setShowVerticalLines(false);
        tbl_indicator.setShowHorizontalLines(false);
        tbl_indicator.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tbl_indicator.setShowGrid(false);
        tbl_indicator.setAutoCreateRowSorter(true);
        scrl_indicator.setViewportView(tbl_indicator);

        final JPanel pnl_close = new JPanel();
        add(pnl_close, BorderLayout.SOUTH);

        btn_close = new JButton("Close");
        pnl_close.add(btn_close);
    }
}