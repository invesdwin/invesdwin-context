package de.invesdwin.context.integration.csv;

import javax.annotation.concurrent.NotThreadSafe;

import org.springframework.batch.item.file.transform.DefaultFieldSetFactory;
import org.springframework.batch.item.file.transform.FieldSet;

@NotThreadSafe
public abstract class AReplacingFieldSetFactory extends DefaultFieldSetFactory {

    @Override
    public FieldSet create(final String[] values) {
        return super.create(replaceValues(values));
    }

    @Override
    public FieldSet create(final String[] values, final String[] names) {
        return super.create(replaceValues(values), names);
    }

    private String[] replaceValues(final String[] values) {
        final String[] newValues = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            newValues[i] = replaceValue(values[i]);
        }
        return newValues;
    }

    protected abstract String replaceValue(String value);

}
