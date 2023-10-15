package de.invesdwin.context.integration.script.callback;

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

public interface IScriptTaskReturns {

    void returnByte(byte value);

    void returnByteVector(byte[] value);

    default void returnByteVectorAsList(final List<Byte> value) {
        returnByteVector(Bytes.toArrayVector(value));
    }

    void returnByteMatrix(byte[][] value);

    default void returnByteMatrixAsList(final List<? extends List<Byte>> value) {
        returnByteMatrix(Bytes.toArrayMatrix(value));
    }

    void returnCharacter(char value);

    void returnCharacterVector(char[] value);

    default void returnCharacterVectorAsList(final List<Character> value) {
        returnCharacterVector(Characters.toArrayVector(value));
    }

    void returnCharacterMatrix(char[][] value);

    default void returnCharacterMatrixAsList(final List<? extends List<Character>> value) {
        returnCharacterMatrix(Characters.toArrayMatrix(value));
    }

    void returnString(String value);

    void returnStringVector(String[] value);

    default void returnStringVectorAsList(final List<String> value) {
        returnStringVector(Strings.toArrayVector(value));
    }

    void returnStringMatrix(String[][] value);

    default void returnStringMatrixAsList(final List<? extends List<String>> value) {
        returnStringMatrix(Strings.toArrayMatrix(value));
    }

    void returnFloat(float value);

    void returnFloatVector(float[] value);

    default void returnFloatVectorAsList(final List<Float> value) {
        returnFloatVector(Floats.toArrayVector(value));
    }

    void returnFloatMatrix(float[][] value);

    default void returnFloatMatrixAsList(final List<? extends List<Float>> value) {
        returnFloatMatrix(Floats.toArrayMatrix(value));
    }

    void returnDouble(double value);

    void returnDoubleVector(double[] value);

    default void returnDoubleVectorAsList(final List<Double> value) {
        returnDoubleVector(Doubles.toArrayVector(value));
    }

    void returnDoubleMatrix(double[][] value);

    default void returnDoubleMatrixAsList(final List<? extends List<Double>> value) {
        returnDoubleMatrix(Doubles.toArrayMatrix(value));
    }

    default void returnDecimal(final ADecimal<?> value) {
        returnDouble(value.getDefaultValue());
    }

    default <T extends ADecimal<?>> void returnDecimalVector(final T[] value) {
        returnDoubleVector(Doubles.checkedCastVector(value));
    }

    default void returnDecimalVectorAsList(final List<? extends ADecimal<?>> value) {
        returnDoubleVector(Doubles.checkedCastVector(value));
    }

    default <T extends ADecimal<?>> void returnDecimalMatrix(final T[][] value) {
        returnDoubleMatrix(Doubles.checkedCastMatrix(value));
    }

    default void returnDecimalMatrixAsList(final List<? extends List<? extends ADecimal<?>>> value) {
        returnDoubleMatrix(Doubles.checkedCastMatrix(value));
    }

    void returnShort(short value);

    void returnShortVector(short[] value);

    default void returnShortVectorAsList(final List<Short> value) {
        returnShortVector(Shorts.toArrayVector(value));
    }

    void returnShortMatrix(short[][] value);

    default void returnShortMatrixAsList(final List<? extends List<Short>> value) {
        returnShortMatrix(Shorts.toArrayMatrix(value));
    }

    void returnInteger(int value);

    void returnIntegerVector(int[] value);

    default void returnIntegerVectorAsList(final List<Integer> value) {
        returnIntegerVector(Integers.toArrayVector(value));
    }

    void returnIntegerMatrix(int[][] value);

    default void returnIntegerMatrixAsList(final List<? extends List<Integer>> value) {
        returnIntegerMatrix(Integers.toArrayMatrix(value));
    }

    void returnLong(long value);

    void returnLongVector(long[] value);

    default void returnLongVectorAsList(final List<Long> value) {
        returnLongVector(Longs.toArrayVector(value));
    }

    void returnLongMatrix(long[][] value);

    default void returnLongMatrixAsList(final List<? extends List<Long>> value) {
        returnLongMatrix(Longs.toArrayMatrix(value));
    }

    void returnBoolean(boolean value);

    void returnBooleanVector(boolean[] value);

    default void returnBooleanVectorAsList(final List<Boolean> value) {
        returnBooleanVector(Booleans.toArrayVector(value));
    }

    void returnBooleanMatrix(boolean[][] value);

    default void returnBooleanMatrixAsList(final List<? extends List<Boolean>> value) {
        returnBooleanMatrix(Booleans.toArrayMatrix(value));
    }

    void returnExpression(String expression);

    void returnNull();

}
