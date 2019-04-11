package de.invesdwin.context.jfreechart.panel.helper.config.dialog.expression;

import java.awt.BorderLayout;

import javax.annotation.concurrent.NotThreadSafe;
import javax.swing.JPanel;
import javax.swing.JTextArea;

@NotThreadSafe
public class ExpressionSettingsPanelLayout extends JPanel {

    //CHECKSTYLE:OFF
    public final JTextArea tf_expression;
    //CHECKSTYLE:ON

    public ExpressionSettingsPanelLayout() {
        tf_expression = new JTextArea();
        tf_expression.setWrapStyleWord(true);
        tf_expression.setLineWrap(true);
        add(tf_expression, BorderLayout.CENTER);
    }

}
