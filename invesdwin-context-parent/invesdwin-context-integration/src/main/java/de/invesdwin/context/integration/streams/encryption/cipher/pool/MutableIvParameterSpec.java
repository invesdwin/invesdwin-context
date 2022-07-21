package de.invesdwin.context.integration.streams.encryption.cipher.pool;

import java.lang.reflect.Field;

import javax.annotation.concurrent.NotThreadSafe;
import javax.crypto.spec.IvParameterSpec;

import de.invesdwin.util.lang.reflection.field.UnsafeField;
import de.invesdwin.util.math.Bytes;

/**
 * Defensive copies for security are not needed because a hacker with reflection access to the JVM can anyway manipulate
 * the keys or read them as he wishes from memory. So we optimize it a bit to reduce the amount of copies.
 */
@NotThreadSafe
public class MutableIvParameterSpec extends IvParameterSpec {

    private static final UnsafeField<byte[]> SUPER_IV_FIELD;

    static {
        try {
            final Field superIvField = IvParameterSpec.class.getDeclaredField("iv");
            SUPER_IV_FIELD = new UnsafeField<>(superIvField);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    private final byte[] iv;

    public MutableIvParameterSpec(final byte[] iv) {
        super(Bytes.EMPTY_ARRAY);
        this.iv = iv;
        SUPER_IV_FIELD.put(this, iv);
    }

    @Override
    public byte[] getIV() {
        return iv;
    }

}
