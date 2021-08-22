package de.invesdwin.context.integration.serde.basic;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.context.integration.serde.ISerde;
import de.invesdwin.context.integration.serde.SerdeBaseMethods;
import de.invesdwin.util.lang.buffer.IByteBuffer;
import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.date.FDates;

@Immutable
public class FDateSerde implements ISerde<FDate> {

    public static final FDateSerde GET = new FDateSerde();
    public static final int FIXED_LENGTH = FDate.BYTES;

    @Override
    public FDate fromBytes(final byte[] bytes) {
        return SerdeBaseMethods.fromBytes(this, bytes);
    }

    @Override
    public byte[] toBytes(final FDate obj) {
        return SerdeBaseMethods.toBytes(this, obj, FIXED_LENGTH);
    }

    @Override
    public FDate fromBuffer(final IByteBuffer buffer) {
        return FDates.extractFDate(buffer, 0);
    }

    @Override
    public int toBuffer(final FDate obj, final IByteBuffer buffer) {
        FDates.putFDate(buffer, 0, obj);
        return FIXED_LENGTH;
    }

}
