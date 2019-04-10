package de.invesdwin.context.jfreechart.panel.helper.config.dialog.parameter.modifier;

import java.math.BigDecimal;

import javax.annotation.concurrent.NotThreadSafe;
import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.invesdwin.context.jfreechart.panel.helper.config.series.ISeriesParameter;
import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.math.expression.IExpression;
import de.invesdwin.util.math.expression.eval.ConstantExpression;
import de.invesdwin.util.swing.spinner.JSpinnerDecimal;

@NotThreadSafe
public class DoubleParameterSettingsModifier extends AParameterSettingsModifier {
    private final JSpinnerDecimal component;

    public DoubleParameterSettingsModifier(final ISeriesParameter parameter, final Runnable modificationListener) {
        super(parameter);

        this.component = new JSpinnerDecimal();
        this.component.getModel().addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(final ChangeEvent e) {

                DoubleParameterSettingsModifier.super.setValue(
                        new ConstantExpression(Doubles.checkedCast(component.getValue())));
                modificationListener.run();
            }
        });
    }

    @Override
    public void setValue(final IExpression value) {
        super.setValue(value);
        component.setValue(new BigDecimal(value.toString()));
    }

    @Override
    public JComponent getComponent() {
        return component;
    }

}
