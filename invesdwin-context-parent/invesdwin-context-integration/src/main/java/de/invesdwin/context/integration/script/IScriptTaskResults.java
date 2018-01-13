package de.invesdwin.context.integration.script;

import java.util.List;

import de.invesdwin.util.lang.Strings;
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

public interface IScriptTaskResults {

    boolean isDefined(String variable);

    default boolean isNotDefined(final String variable) {
        return !isDefined(variable);
    }

    boolean isNull(String variable);

    default boolean isNotNull(final String variable) {
        return !isNull(variable);
    }

    IScriptTaskEngine getEngine();

    byte getByte(String variable);

    byte[] getByteVector(String variable);

    default List<Byte> getByteVectorAsList(final String variable) {
        return Bytes.asList(getByteVector(variable));
    }

    byte[][] getByteMatrix(String variable);

    default List<List<Byte>> getByteMatrixAsList(final String variable) {
        return Bytes.asListMatrix(getByteMatrix(variable));
    }

    char getCharacter(String variable);

    char[] getCharacterVector(String variable);

    default List<Character> getCharacterVectorAsList(final String variable) {
        return Characters.asList(getCharacterVector(variable));
    }

    char[][] getCharacterMatrix(String variable);

    default List<List<Character>> getCharacterMatrixAsList(final String variable) {
        return Characters.asListMatrix(getCharacterMatrix(variable));
    }

    String getString(String variable);

    String[] getStringVector(String variable);

    default List<String> getStringVectorAsList(final String variable) {
        return Strings.asList(getStringVector(variable));
    }

    String[][] getStringMatrix(String variable);

    default List<List<String>> getStringMatrixAsList(final String variable) {
        return Strings.asListMatrix(getStringMatrix(variable));
    }

    float getFloat(String variable);

    float[] getFloatVector(String variable);

    default List<Float> getFloatVectorAsList(final String variable) {
        return Floats.asList(getFloatVector(variable));
    }

    float[][] getFloatMatrix(String variable);

    default List<List<Float>> getFloatMatrixAsList(final String variable) {
        return Floats.asListMatrix(getFloatMatrix(variable));
    }

    double getDouble(String variable);

    double[] getDoubleVector(String variable);

    default List<Double> getDoubleVectorAsList(final String variable) {
        return Doubles.asList(getDoubleVector(variable));
    }

    double[][] getDoubleMatrix(String variable);

    default List<List<Double>> getDoubleMatrixAsList(final String variable) {
        final double[][] matrix = getDoubleMatrix(variable);
        return Doubles.asListMatrix(matrix);
    }

    default Decimal getDecimal(final String variable) {
        return getDecimal(variable, Decimal.ZERO);
    }

    default Decimal[] getDecimalVector(final String variable) {
        return getDecimalVector(variable, Decimal.ZERO);
    }

    default List<Decimal> getDecimalVectorAsList(final String variable) {
        return getDecimalVectorAsList(variable, Decimal.ZERO);
    }

    default Decimal[][] getDecimalMatrix(final String variable) {
        return getDecimalMatrix(variable, Decimal.ZERO);
    }

    default List<List<Decimal>> getDecimalMatrixAsList(final String variable) {
        return getDecimalMatrixAsList(variable, Decimal.ZERO);
    }

    default <T extends ADecimal<T>> T getDecimal(final String variable, final T converter) {
        return converter.toObject(getDouble(variable));
    }

    default <T extends ADecimal<T>> T[] getDecimalVector(final String variable, final T converter) {
        return converter.toObjectVector(getDoubleVector(variable));
    }

    default <T extends ADecimal<T>> List<T> getDecimalVectorAsList(final String variable, final T converter) {
        return ADecimal.asListVector(getDecimalVector(variable, converter));
    }

    default <T extends ADecimal<T>> T[][] getDecimalMatrix(final String variable, final T converter) {
        return converter.toObjectMatrix(getDoubleMatrix(variable));
    }

    default <T extends ADecimal<T>> List<List<T>> getDecimalMatrixAsList(final String variable, final T converter) {
        return ADecimal.asListMatrix(getDecimalMatrix(variable, converter));
    }

    short getShort(String variable);

    short[] getShortVector(String variable);

    default List<Short> getShortVectorAsList(final String variable) {
        return Shorts.asList(getShortVector(variable));
    }

    short[][] getShortMatrix(String variable);

    default List<List<Short>> getShortMatrixAsList(final String variable) {
        return Shorts.asListMatrix(getShortMatrix(variable));
    }

    int getInteger(String variable);

    int[] getIntegerVector(String variable);

    default List<Integer> getIntegerVectorAsList(final String variable) {
        return Integers.asList(getIntegerVector(variable));
    }

    int[][] getIntegerMatrix(String variable);

    default List<List<Integer>> getIntegerMatrixAsList(final String variable) {
        return Integers.asListMatrix(getIntegerMatrix(variable));
    }

    long getLong(String variable);

    long[] getLongVector(String variable);

    default List<Long> getLongVectorAsList(final String variable) {
        return Longs.asList(getLongVector(variable));
    }

    long[][] getLongMatrix(String variable);

    default List<List<Long>> getLongMatrixAsList(final String variable) {
        return Longs.asListMatrix(getLongMatrix(variable));
    }

    boolean getBoolean(String variable);

    boolean[] getBooleanVector(String variable);

    default List<Boolean> getBooleanVectorAsList(final String variable) {
        return Booleans.asList(getBooleanVector(variable));
    }

    boolean[][] getBooleanMatrix(String variable);

    default List<List<Boolean>> getBooleanMatrixAsList(final String variable) {
        return Booleans.asListMatrix(getBooleanMatrix(variable));
    }

}
