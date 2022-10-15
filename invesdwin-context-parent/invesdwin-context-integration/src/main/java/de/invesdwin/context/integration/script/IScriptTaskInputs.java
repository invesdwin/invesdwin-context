package de.invesdwin.context.integration.script;

import java.util.List;

import de.invesdwin.util.lang.string.Strings;
import de.invesdwin.util.math.Booleans;
import de.invesdwin.util.math.Bytes;
import de.invesdwin.util.math.Characters;
import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.math.Floats;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.math.Longs;
import de.invesdwin.util.math.Shorts;
import de.invesdwin.util.math.decimal.ADecimal;

public interface IScriptTaskInputs {

    IScriptTaskEngine getEngine();

    void putByte(String variable, byte value);

    void putByteVector(String variable, byte[] value);

    default void putByteVectorAsList(final String variable, final List<Byte> value) {
        putByteVector(variable, Bytes.toArrayVector(value));
    }

    void putByteMatrix(String variable, byte[][] value);

    default void putByteMatrixAsList(final String variable, final List<? extends List<Byte>> value) {
        putByteMatrix(variable, Bytes.toArrayMatrix(value));
    }

    void putCharacter(String variable, char value);

    void putCharacterVector(String variable, char[] value);

    default void putCharacterVectorAsList(final String variable, final List<Character> value) {
        putCharacterVector(variable, Characters.toArrayVector(value));
    }

    void putCharacterMatrix(String variable, char[][] value);

    default void putCharacterMatrixAsList(final String variable, final List<? extends List<Character>> value) {
        putCharacterMatrix(variable, Characters.toArrayMatrix(value));
    }

    void putString(String variable, String value);

    void putStringVector(String variable, String[] value);

    default void putStringVectorAsList(final String variable, final List<String> value) {
        putStringVector(variable, Strings.toArrayVector(value));
    }

    void putStringMatrix(String variable, String[][] value);

    default void putStringMatrixAsList(final String variable, final List<? extends List<String>> value) {
        putStringMatrix(variable, Strings.toArrayMatrix(value));
    }

    void putFloat(String variable, float value);

    void putFloatVector(String variable, float[] value);

    default void putFloatVectorAsList(final String variable, final List<Float> value) {
        putFloatVector(variable, Floats.toArrayVector(value));
    }

    void putFloatMatrix(String variable, float[][] value);

    default void putFloatMatrixAsList(final String variable, final List<? extends List<Float>> value) {
        putFloatMatrix(variable, Floats.toArrayMatrix(value));
    }

    void putDouble(String variable, double value);

    void putDoubleVector(String variable, double[] value);

    default void putDoubleVectorAsList(final String variable, final List<Double> value) {
        putDoubleVector(variable, Doubles.toArrayVector(value));
    }

    void putDoubleMatrix(String variable, double[][] value);

    default void putDoubleMatrixAsList(final String variable, final List<? extends List<Double>> value) {
        putDoubleMatrix(variable, Doubles.toArrayMatrix(value));
    }

    default void putDecimal(final String variable, final ADecimal<?> value) {
        putDouble(variable, value.getDefaultValue());
    }

    default <T extends ADecimal<?>> void putDecimalVector(final String variable, final T[] value) {
        putDoubleVector(variable, Doubles.checkedCastVector(value));
    }

    default void putDecimalVectorAsList(final String variable, final List<? extends ADecimal<?>> value) {
        putDoubleVector(variable, Doubles.checkedCastVector(value));
    }

    default <T extends ADecimal<?>> void putDecimalMatrix(final String variable, final T[][] value) {
        putDoubleMatrix(variable, Doubles.checkedCastMatrix(value));
    }

    default void putDecimalMatrixAsList(final String variable,
            final List<? extends List<? extends ADecimal<?>>> value) {
        putDoubleMatrix(variable, Doubles.checkedCastMatrix(value));
    }

    void putShort(String variable, short value);

    void putShortVector(String variable, short[] value);

    default void putShortVectorAsList(final String variable, final List<Short> value) {
        putShortVector(variable, Shorts.toArrayVector(value));
    }

    void putShortMatrix(String variable, short[][] value);

    default void putShortMatrixAsList(final String variable, final List<? extends List<Short>> value) {
        putShortMatrix(variable, Shorts.toArrayMatrix(value));
    }

    void putInteger(String variable, int value);

    void putIntegerVector(String variable, int[] value);

    default void putIntegerVectorAsList(final String variable, final List<Integer> value) {
        putIntegerVector(variable, Integers.toArrayVector(value));
    }

    void putIntegerMatrix(String variable, int[][] value);

    default void putIntegerMatrixAsList(final String variable, final List<? extends List<Integer>> value) {
        putIntegerMatrix(variable, Integers.toArrayMatrix(value));
    }

    void putLong(String variable, long value);

    void putLongVector(String variable, long[] value);

    default void putLongVectorAsList(final String variable, final List<Long> value) {
        putLongVector(variable, Longs.toArrayVector(value));
    }

    void putLongMatrix(String variable, long[][] value);

    default void putLongMatrixAsList(final String variable, final List<? extends List<Long>> value) {
        putLongMatrix(variable, Longs.toArrayMatrix(value));
    }

    void putBoolean(String variable, boolean value);

    void putBooleanVector(String variable, boolean[] value);

    default void putBooleanVectorAsList(final String variable, final List<Boolean> value) {
        putBooleanVector(variable, Booleans.toArrayVector(value));
    }

    void putBooleanMatrix(String variable, boolean[][] value);

    default void putBooleanMatrixAsList(final String variable, final List<? extends List<Boolean>> value) {
        putBooleanMatrix(variable, Booleans.toArrayMatrix(value));
    }

    void putExpression(String variable, String expression);

    void putNull(String variable);

    void remove(String variable);

}
