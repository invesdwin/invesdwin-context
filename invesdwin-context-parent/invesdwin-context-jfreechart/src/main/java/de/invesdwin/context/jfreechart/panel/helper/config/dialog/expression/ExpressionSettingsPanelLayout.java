package de.invesdwin.context.jfreechart.panel.helper.config.dialog.expression;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.annotation.concurrent.NotThreadSafe;
import javax.swing.JButton;
import javax.swing.JPanel;

import de.invesdwin.context.jfreechart.panel.helper.config.series.AddSeriesPanelLayout;
import de.invesdwin.util.swing.icon.JIconTextField;

@NotThreadSafe
public class ExpressionSettingsPanelLayout extends JPanel {

    //CHECKSTYLE:OFF
    public final JIconTextField tf_expression;
    public final JButton btn_applyExpression;
    //CHECKSTYLE:ON

    public ExpressionSettingsPanelLayout() {
        setLayout(new BorderLayout(5, 5));
        tf_expression = new JIconTextField();
        tf_expression.setIcon(AddSeriesPanelLayout.ICON_EXPRESSION);
        tf_expression.setMinimumSize(new Dimension(300, AddSeriesPanelLayout.DIMENSION_TEXTFIELD.height));
        tf_expression.setPreferredSize(tf_expression.getMinimumSize());
        tf_expression.setPreferredSize(tf_expression.getMinimumSize());
        add(tf_expression, BorderLayout.CENTER);

        btn_applyExpression = new JButton("Apply");
        add(btn_applyExpression, BorderLayout.EAST);
    }

}
