package de.invesdwin.context.integration.serde.basic;

import java.util.Date;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.context.integration.serde.ISerde;

@Immutable
public class DateSerde implements ISerde<Date> {

    public static final DateSerde GET = new DateSerde();
    public static final int FIXED_LENGTH = Long.BYTES;

    @Override
    public Date fromBytes(final byte[] bytes) {
        final Long time = LongSerde.GET.fromBytes(bytes);
        if (time == null) {
            return null;
        } else {
            //CHECKSTYLE:OFF
            return new Date(time);
            //CHECKSTYLE:ON
        }
    }

    @Override
    public byte[] toBytes(final Date obj) {
        final Long time;
        if (obj == null) {
            time = null;
        } else {
            time = obj.getTime();
        }
        return LongSerde.GET.toBytes(time);
    }

}