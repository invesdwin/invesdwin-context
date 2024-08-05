package de.invesdwin.context.jfreechart;

import java.lang.reflect.Field;

import javax.annotation.concurrent.Immutable;

import org.jfree.chart.LegendItem;

import de.invesdwin.util.lang.reflection.field.UnsafeField;

@Immutable
public final class LegendItems {

    private static final UnsafeField<String> FIELD_LABEL;

    static {
        try {
            final Field fieldLabel = LegendItem.class.getDeclaredField("label");
            FIELD_LABEL = new UnsafeField<String>(fieldLabel);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    private LegendItems() {}

    public static void setLabel(final LegendItem legendItem, final String label) {
        FIELD_LABEL.put(legendItem, label);
    }

}
