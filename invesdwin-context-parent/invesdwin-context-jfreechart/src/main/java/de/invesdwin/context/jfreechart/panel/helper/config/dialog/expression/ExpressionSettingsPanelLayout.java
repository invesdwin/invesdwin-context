package de.invesdwin.context.jfreechart.panel.helper.config.dialog.expression;

import java.awt.BorderLayout;

import javax.annotation.concurrent.NotThreadSafe;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

@NotThreadSafe
public class ExpressionSettingsPanelLayout extends JPanel {

    //CHECKSTYLE:OFF
    public final JTextArea ta_expression;
    public final JButton btn_apply;
    //CHECKSTYLE:ON

    public ExpressionSettingsPanelLayout() {
        setLayout(new BorderLayout());

        final JScrollPane scrl_expression = new JScrollPane();
        add(scrl_expression, BorderLayout.CENTER);
        ta_expression = new JTextArea();
        ta_expression.setWrapStyleWord(true);
        ta_expression.setLineWrap(true);

        scrl_expression.setViewportView(ta_expression);

        final JPanel pnl_apply = new JPanel();
        add(pnl_apply, BorderLayout.SOUTH);

        btn_apply = new JButton("Apply");
        pnl_apply.add(btn_apply);
    }

}
