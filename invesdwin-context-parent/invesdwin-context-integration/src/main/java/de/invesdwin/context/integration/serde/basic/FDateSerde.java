package de.invesdwin.context.integration.serde.basic;

import java.nio.ByteBuffer;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.context.integration.serde.ISerde;
import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.date.FDates;

@Immutable
public class FDateSerde implements ISerde<FDate> {

    public static final FDateSerde GET = new FDateSerde();
    public static final int FIXED_LENGTH = FDate.BYTES;

    @Override
    public FDate fromBytes(final byte[] bytes) {
        final ByteBuffer buf = ByteBuffer.wrap(bytes);
        return FDates.extractFDate(buf);
    }

    @Override
    public byte[] toBytes(final FDate obj) {
        final ByteBuffer buf = ByteBuffer.allocate(FIXED_LENGTH);
        FDates.putFDate(buf, obj);
        return buf.array();
    }

}
