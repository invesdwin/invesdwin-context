package de.invesdwin.context.integration.serde.basic;

import java.util.Calendar;
import java.util.Date;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.context.integration.serde.ISerde;

@Immutable
public class CalendarSerde implements ISerde<Calendar> {

    public static final CalendarSerde GET = new CalendarSerde();
    public static final int FIXED_LENGTH = DateSerde.FIXED_LENGTH;

    @Override
    public Calendar fromBytes(final byte[] bytes) {
        final Date date = DateSerde.GET.fromBytes(bytes);
        if (date == null) {
            return null;
        } else {
            //CHECKSTYLE:OFF
            final Calendar cal = Calendar.getInstance();
            //CHECKSTYLE:ON
            cal.setTime(date);
            return cal;
        }
    }

    @Override
    public byte[] toBytes(final Calendar obj) {
        final Date date;
        if (obj == null) {
            date = null;
        } else {
            date = obj.getTime();
        }
        return DateSerde.GET.toBytes(date);
    }

}
