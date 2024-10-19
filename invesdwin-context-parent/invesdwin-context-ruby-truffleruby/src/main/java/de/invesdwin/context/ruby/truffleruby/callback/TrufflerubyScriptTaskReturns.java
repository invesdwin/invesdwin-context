package de.invesdwin.context.ruby.truffleruby.callback;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.context.integration.script.callback.ObjectScriptTaskReturns;
import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.math.Integers;

@NotThreadSafe
public class TrufflerubyScriptTaskReturns extends ObjectScriptTaskReturns {

    @Override
    public void returnByte(final byte value) {
        returnInteger(Integers.checkedCast(value));
    }

    @Override
    public void returnByteVector(final byte[] value) {
        returnIntegerVector(Integers.checkedCastVector(value));
    }

    @Override
    public void returnByteMatrix(final byte[][] value) {
        returnIntegerMatrix(Integers.checkedCastMatrix(value));
    }

    @Override
    public void returnCharacter(final char value) {
        returnInteger(Integers.checkedCast(value));
    }

    @Override
    public void returnCharacterVector(final char[] value) {
        returnIntegerVector(Integers.checkedCastVector(value));
    }

    @Override
    public void returnCharacterMatrix(final char[][] value) {
        returnIntegerMatrix(Integers.checkedCastMatrix(value));
    }

    @Override
    public void returnShort(final short value) {
        returnInteger(Integers.checkedCast(value));
    }

    @Override
    public void returnShortVector(final short[] value) {
        returnIntegerVector(Integers.checkedCastVector(value));
    }

    @Override
    public void returnShortMatrix(final short[][] value) {
        returnIntegerMatrix(Integers.checkedCastMatrix(value));
    }

    @Override
    public void returnFloat(final float value) {
        returnDouble(Doubles.checkedCast(value));
    }

    @Override
    public void returnFloatVector(final float[] value) {
        returnDoubleVector(Doubles.checkedCastVector(value));
    }

    @Override
    public void returnFloatMatrix(final float[][] value) {
        returnDoubleMatrix(Doubles.checkedCastMatrix(value));
    }

}
