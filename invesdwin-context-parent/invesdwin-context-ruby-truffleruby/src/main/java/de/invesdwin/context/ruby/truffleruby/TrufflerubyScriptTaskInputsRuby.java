package de.invesdwin.context.ruby.truffleruby;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.context.ruby.IScriptTaskInputsRuby;
import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.math.Integers;

/**
 * org.truffleruby.language.RubyGuards.assertIsValidRubyValue: Byte, Short, Float not supported
 *
 * @author subes
 *
 */
@NotThreadSafe
public class TrufflerubyScriptTaskInputsRuby implements IScriptTaskInputsRuby {

    private final TrufflerubyScriptTaskEngineRuby engine;

    public TrufflerubyScriptTaskInputsRuby(final TrufflerubyScriptTaskEngineRuby engine) {
        this.engine = engine;
    }

    @Override
    public TrufflerubyScriptTaskEngineRuby getEngine() {
        return engine;
    }

    @Override
    public void putByte(final String variable, final byte value) {
        putInteger(variable, Integers.checkedCast(value));
    }

    @Override
    public void putByteVector(final String variable, final byte[] value) {
        putIntegerVector(variable, Integers.checkedCastVector(value));
    }

    @Override
    public void putByteMatrix(final String variable, final byte[][] value) {
        putIntegerMatrix(variable, Integers.checkedCastMatrix(value));
    }

    @Override
    public void putCharacter(final String variable, final char value) {
        putInteger(variable, Integers.checkedCast(value));
    }

    @Override
    public void putCharacterVector(final String variable, final char[] value) {
        putIntegerVector(variable, Integers.checkedCastVector(value));
    }

    @Override
    public void putCharacterMatrix(final String variable, final char[][] value) {
        putIntegerMatrix(variable, Integers.checkedCastMatrix(value));
    }

    @Override
    public void putString(final String variable, final String value) {
        engine.unwrap().put(variable, value);
    }

    @Override
    public void putStringVector(final String variable, final String[] value) {
        engine.unwrap().put(variable, value);
    }

    @Override
    public void putStringMatrix(final String variable, final String[][] value) {
        engine.unwrap().put(variable, value);
    }

    @Override
    public void putFloat(final String variable, final float value) {
        putDouble(variable, Doubles.checkedCast(value));
    }

    @Override
    public void putFloatVector(final String variable, final float[] value) {
        putDoubleVector(variable, Doubles.checkedCastVector(value));
    }

    @Override
    public void putFloatMatrix(final String variable, final float[][] value) {
        putDoubleMatrix(variable, Doubles.checkedCastMatrix(value));
    }

    @Override
    public void putDouble(final String variable, final double value) {
        engine.unwrap().put(variable, value);
    }

    @Override
    public void putDoubleVector(final String variable, final double[] value) {
        engine.unwrap().put(variable, value);
    }

    @Override
    public void putDoubleMatrix(final String variable, final double[][] value) {
        engine.unwrap().put(variable, value);
    }

    @Override
    public void putShort(final String variable, final short value) {
        putInteger(variable, Integers.checkedCast(value));
    }

    @Override
    public void putShortVector(final String variable, final short[] value) {
        putIntegerVector(variable, Integers.checkedCastVector(value));
    }

    @Override
    public void putShortMatrix(final String variable, final short[][] value) {
        putIntegerMatrix(variable, Integers.checkedCastMatrix(value));
    }

    @Override
    public void putInteger(final String variable, final int value) {
        engine.unwrap().put(variable, value);
    }

    @Override
    public void putIntegerVector(final String variable, final int[] value) {
        engine.unwrap().put(variable, value);
    }

    @Override
    public void putIntegerMatrix(final String variable, final int[][] value) {
        engine.unwrap().put(variable, value);
    }

    @Override
    public void putLong(final String variable, final long value) {
        engine.unwrap().put(variable, value);
    }

    @Override
    public void putLongVector(final String variable, final long[] value) {
        engine.unwrap().put(variable, value);
    }

    @Override
    public void putLongMatrix(final String variable, final long[][] value) {
        engine.unwrap().put(variable, value);
    }

    @Override
    public void putBoolean(final String variable, final boolean value) {
        engine.unwrap().put(variable, value);
    }

    @Override
    public void putBooleanVector(final String variable, final boolean[] value) {
        engine.unwrap().put(variable, value);
    }

    @Override
    public void putBooleanMatrix(final String variable, final boolean[][] value) {
        engine.unwrap().put(variable, value);
    }

    @Override
    public void putExpression(final String variable, final String expression) {
        engine.unwrap().eval(variable + " = " + expression);
    }

    @Override
    public void putNull(final String variable) {
        engine.unwrap().put(variable, null);
    }

    @Override
    public void remove(final String variable) {
        try {
            engine.unwrap().remove(variable);
        } catch (final UnsupportedOperationException e) {
            //ignore
        }
    }

}
