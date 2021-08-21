package de.invesdwin.context.integration.serde;

import java.util.Calendar;
import java.util.Date;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.context.integration.serde.basic.BooleanSerde;
import de.invesdwin.context.integration.serde.basic.ByteSerde;
import de.invesdwin.context.integration.serde.basic.CalendarSerde;
import de.invesdwin.context.integration.serde.basic.DateSerde;
import de.invesdwin.context.integration.serde.basic.DecimalSerde;
import de.invesdwin.context.integration.serde.basic.DoubleSerde;
import de.invesdwin.context.integration.serde.basic.FDateSerde;
import de.invesdwin.context.integration.serde.basic.IntegerSerde;
import de.invesdwin.context.integration.serde.basic.LongSerde;
import de.invesdwin.context.integration.serde.basic.StringSerde;
import de.invesdwin.context.integration.serde.basic.TimedDecimalSerde;
import de.invesdwin.context.integration.serde.basic.VoidSerde;
import de.invesdwin.util.lang.reflection.Reflections;
import de.invesdwin.util.math.decimal.Decimal;
import de.invesdwin.util.math.decimal.TimedDecimal;
import de.invesdwin.util.time.date.FDate;

@Immutable
public class TypeDelegateSerde<O> implements ISerde<O> {

    private final ISerde<O> delegate;

    @SuppressWarnings("unchecked")
    public TypeDelegateSerde(final Class<O> type) {
        delegate = (ISerde<O>) newDelegate(type);
    }

    //CHECKSTYLE:OFF
    protected ISerde<?> newDelegate(final Class<O> type) {
        //CHECKSTYLE:ON
        if (Reflections.isVoid(type)) {
            return VoidSerde.GET;
        }
        if (double.class.isAssignableFrom(type) || Double.class.isAssignableFrom(type)) {
            return DoubleSerde.GET;
        }
        if (Decimal.class.isAssignableFrom(type)) {
            return DecimalSerde.GET;
        }
        if (FDate.class.isAssignableFrom(type)) {
            return FDateSerde.GET;
        }
        if (Boolean.class.isAssignableFrom(type) || boolean.class.isAssignableFrom(type)) {
            return BooleanSerde.GET;
        }
        if (Integer.class.isAssignableFrom(type) || int.class.isAssignableFrom(type)) {
            return IntegerSerde.GET;
        }
        if (TimedDecimal.class.isAssignableFrom(type)) {
            return TimedDecimalSerde.GET;
        } else if (Byte.class.isAssignableFrom(type) || byte.class.isAssignableFrom(type)) {
            return ByteSerde.GET;
        } else if (Date.class.isAssignableFrom(type)) {
            return DateSerde.GET;
        } else if (Long.class.isAssignableFrom(type) || long.class.isAssignableFrom(type)) {
            return LongSerde.GET;
        } else if (Integer.class.isAssignableFrom(type) || int.class.isAssignableFrom(type)) {
            return IntegerSerde.GET;
        } else if (Calendar.class.isAssignableFrom(type)) {
            return CalendarSerde.GET;
        } else if (String.class.isAssignableFrom(type)) {
            return StringSerde.GET;
        } else if (Void.class.isAssignableFrom(type) || void.class.isAssignableFrom(type)) {
            return VoidSerde.GET;
        } else {
            // fallback to slower serialization
            // do not check type to gracefully fall back on interface types like List
            return new RemoteFastSerializingSerde<O>(true, type);
        }
    }

    @Override
    public O fromBytes(final byte[] bytes) {
        return delegate.fromBytes(bytes);
    }

    @Override
    public byte[] toBytes(final O obj) {
        return delegate.toBytes(obj);
    }

}
