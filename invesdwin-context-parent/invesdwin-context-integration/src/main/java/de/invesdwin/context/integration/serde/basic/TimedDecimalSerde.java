package de.invesdwin.context.integration.serde.basic;

import java.nio.ByteBuffer;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.context.integration.serde.FixedLengthBufferingIteratorDelegateSerde;
import de.invesdwin.context.integration.serde.ISerde;
import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.math.decimal.TimedDecimal;
import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.date.FDates;

@Immutable
public class TimedDecimalSerde implements ISerde<TimedDecimal> {

    public static final TimedDecimalSerde GET = new TimedDecimalSerde();
    public static final Integer FIXED_LENGTH = 8 + 8;

    public static final FixedLengthBufferingIteratorDelegateSerde<TimedDecimal> GET_LIST = new FixedLengthBufferingIteratorDelegateSerde<TimedDecimal>(
            GET, FIXED_LENGTH);

    static {
        Assertions.assertThat(GET.fromBytes(GET.toBytes(TimedDecimal.DUMMY))).isEqualTo(TimedDecimal.DUMMY);
    }

    public TimedDecimalSerde() {
    }

    @Override
    public TimedDecimal fromBytes(final byte[] bytes) {
        final ByteBuffer buffer = ByteBuffer.wrap(bytes);
        final FDate time = FDates.extractFDate(buffer);
        final double value = buffer.getDouble();

        final TimedDecimal timedMoney = new TimedDecimal(time, value);
        return timedMoney;
    }

    @Override
    public byte[] toBytes(final TimedDecimal obj) {
        final ByteBuffer buffer = ByteBuffer.allocate(16);
        FDates.putFDate(buffer, obj.getTime());
        buffer.putDouble(obj.doubleValue());
        return buffer.array();
    }

}
