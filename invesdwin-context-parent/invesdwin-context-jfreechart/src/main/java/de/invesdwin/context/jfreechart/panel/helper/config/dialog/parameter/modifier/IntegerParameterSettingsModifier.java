package de.invesdwin.context.jfreechart.panel.helper.config.dialog.parameter.modifier;

import javax.annotation.concurrent.NotThreadSafe;
import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.invesdwin.context.jfreechart.panel.helper.config.series.ISeriesParameter;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.math.expression.IExpression;
import de.invesdwin.util.math.expression.eval.ConstantExpression;
import de.invesdwin.util.swing.spinner.JSpinnerDecimal;
import de.invesdwin.util.swing.spinner.JSpinnerInteger;

@NotThreadSafe
public class IntegerParameterSettingsModifier extends AParameterSettingsModifier {

    private final JSpinnerDecimal component;

    public IntegerParameterSettingsModifier(final ISeriesParameter parameter, final Runnable modificationListener) {
        super(parameter);

        this.component = new JSpinnerInteger();
        this.component.getModel().addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                IntegerParameterSettingsModifier.super.setValue(
                        new ConstantExpression(Integers.checkedCast(component.getValue())));
                modificationListener.run();
            }
        });
    }

    @Override
    public void setValue(final IExpression value) {
        super.setValue(value);
        component.setValue(value.evaluateInteger());
    }

    @Override
    public JComponent getComponent() {
        return component;
    }

}
