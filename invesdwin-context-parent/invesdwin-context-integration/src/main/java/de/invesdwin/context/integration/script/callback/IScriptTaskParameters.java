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
import de.invesdwin.util.math.decimal.Decimal;

public interface IScriptTaskParameters {

    int size();

    boolean isNull(int index);

    default boolean isNotNull(final int index) {
        return !isNull(index);
    }

    byte getByte(int index);

    byte[] getByteVector(int index);

    default List<Byte> getByteVectorAsList(final int index) {
        return Bytes.asList(getByteVector(index));
    }

    byte[][] getByteMatrix(int index);

    default List<List<Byte>> getByteMatrixAsList(final int index) {
        return Bytes.asListMatrix(getByteMatrix(index));
    }

    char getCharacter(int index);

    char[] getCharacterVector(int index);

    default List<Character> getCharacterVectorAsList(final int index) {
        return Characters.asList(getCharacterVector(index));
    }

    char[][] getCharacterMatrix(int index);

    default List<List<Character>> getCharacterMatrixAsList(final int index) {
        return Characters.asListMatrix(getCharacterMatrix(index));
    }

    String getString(int index);

    String[] getStringVector(int index);

    default List<String> getStringVectorAsList(final int index) {
        return Strings.asList(getStringVector(index));
    }

    String[][] getStringMatrix(int index);

    default List<List<String>> getStringMatrixAsList(final int index) {
        return Strings.asListMatrix(getStringMatrix(index));
    }

    float getFloat(int index);

    float[] getFloatVector(int index);

    default List<Float> getFloatVectorAsList(final int index) {
        return Floats.asList(getFloatVector(index));
    }

    float[][] getFloatMatrix(int index);

    default List<List<Float>> getFloatMatrixAsList(final int index) {
        return Floats.asListMatrix(getFloatMatrix(index));
    }

    double getDouble(int index);

    double[] getDoubleVector(int index);

    default List<Double> getDoubleVectorAsList(final int index) {
        return Doubles.asList(getDoubleVector(index));
    }

    double[][] getDoubleMatrix(int index);

    default List<List<Double>> getDoubleMatrixAsList(final int index) {
        final double[][] matrix = getDoubleMatrix(index);
        return Doubles.asListMatrix(matrix);
    }

    default Decimal getDecimal(final int index) {
        return getDecimal(index, Decimal.ZERO);
    }

    default Decimal[] getDecimalVector(final int index) {
        return getDecimalVector(index, Decimal.ZERO);
    }

    default List<Decimal> getDecimalVectorAsList(final int index) {
        return getDecimalVectorAsList(index, Decimal.ZERO);
    }

    default Decimal[][] getDecimalMatrix(final int index) {
        return getDecimalMatrix(index, Decimal.ZERO);
    }

    default List<List<Decimal>> getDecimalMatrixAsList(final int index) {
        return getDecimalMatrixAsList(index, Decimal.ZERO);
    }

    default <T extends ADecimal<T>> T getDecimal(final int index, final T converter) {
        return converter.toObject(getDouble(index));
    }

    default <T extends ADecimal<T>> T[] getDecimalVector(final int index, final T converter) {
        return converter.toObjectVector(getDoubleVector(index));
    }

    default <T extends ADecimal<T>> List<T> getDecimalVectorAsList(final int index, final T converter) {
        return ADecimal.asListVector(getDecimalVector(index, converter));
    }

    default <T extends ADecimal<T>> T[][] getDecimalMatrix(final int index, final T converter) {
        return converter.toObjectMatrix(getDoubleMatrix(index));
    }

    default <T extends ADecimal<T>> List<List<T>> getDecimalMatrixAsList(final int index, final T converter) {
        return ADecimal.asListMatrix(getDecimalMatrix(index, converter));
    }

    short getShort(int index);

    short[] getShortVector(int index);

    default List<Short> getShortVectorAsList(final int index) {
        return Shorts.asList(getShortVector(index));
    }

    short[][] getShortMatrix(int index);

    default List<List<Short>> getShortMatrixAsList(final int index) {
        return Shorts.asListMatrix(getShortMatrix(index));
    }

    int getInteger(int index);

    int[] getIntegerVector(int index);

    default List<Integer> getIntegerVectorAsList(final int index) {
        return Integers.asList(getIntegerVector(index));
    }

    int[][] getIntegerMatrix(int index);

    default List<List<Integer>> getIntegerMatrixAsList(final int index) {
        return Integers.asListMatrix(getIntegerMatrix(index));
    }

    long getLong(int index);

    long[] getLongVector(int index);

    default List<Long> getLongVectorAsList(final int index) {
        return Longs.asList(getLongVector(index));
    }

    long[][] getLongMatrix(int index);

    default List<List<Long>> getLongMatrixAsList(final int index) {
        return Longs.asListMatrix(getLongMatrix(index));
    }

    boolean getBoolean(int index);

    boolean[] getBooleanVector(int index);

    default List<Boolean> getBooleanVectorAsList(final int index) {
        return Booleans.asList(getBooleanVector(index));
    }

    boolean[][] getBooleanMatrix(int index);

    default List<List<Boolean>> getBooleanMatrixAsList(final int index) {
        return Booleans.asListMatrix(getBooleanMatrix(index));
    }

}
