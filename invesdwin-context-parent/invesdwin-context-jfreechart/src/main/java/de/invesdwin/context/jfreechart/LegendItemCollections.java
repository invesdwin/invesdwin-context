package de.invesdwin.context.jfreechart;

import java.lang.reflect.Field;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;

import de.invesdwin.util.lang.reflection.field.UnsafeField;

@Immutable
public final class LegendItemCollections {

    private static final UnsafeField<List<LegendItem>> FIELD_ITEMS;

    static {
        try {
            final Field fieldItems = LegendItemCollection.class.getDeclaredField("items");
            FIELD_ITEMS = new UnsafeField<List<LegendItem>>(fieldItems);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    private LegendItemCollections() {}

    public static List<LegendItem> getItems(final LegendItemCollection legendItems) {
        return FIELD_ITEMS.get(legendItems);
    }

}
