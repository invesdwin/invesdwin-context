package de.invesdwin.context.integration.script.callback;

import java.io.Closeable;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.lang.string.Strings;
import de.invesdwin.util.math.Booleans;
import de.invesdwin.util.math.Bytes;
import de.invesdwin.util.math.Characters;
import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.math.Floats;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.math.Longs;
import de.invesdwin.util.math.Shorts;

@NotThreadSafe
public class ObjectScriptTaskParameters implements IScriptTaskParameters, Closeable {

    private Object[] parameters;

    public void setParameters(final Object[] parameters) {
        this.parameters = parameters;
    }

    protected Object getParameter(final int index) {
        return parameters[index];
    }

    @Override
    public int size() {
        return parameters.length;
    }

    @Override
    public boolean isNull(final int index) {
        return getParameter(index) == null;
    }

    @Override
    public byte getByte(final int index) {
        return Bytes.checkedCast(getParameter(index));
    }

    @Override
    public byte[] getByteVector(final int index) {
        return Bytes.checkedCastVector(getParameter(index));
    }

    @Override
    public byte[][] getByteMatrix(final int index) {
        return Bytes.checkedCastMatrix(getParameter(index));
    }

    @Override
    public char getCharacter(final int index) {
        return Characters.checkedCast(getParameter(index));
    }

    @Override
    public char[] getCharacterVector(final int index) {
        return Characters.checkedCastVector(getParameter(index));
    }

    @Override
    public char[][] getCharacterMatrix(final int index) {
        return Characters.checkedCastMatrix(getParameter(index));
    }

    @Override
    public String getString(final int index) {
        return Strings.checkedCast(getParameter(index));
    }

    @Override
    public String[] getStringVector(final int index) {
        return Strings.checkedCastVector(getParameter(index));
    }

    @Override
    public String[][] getStringMatrix(final int index) {
        return Strings.checkedCastMatrix(getParameter(index));
    }

    @Override
    public float getFloat(final int index) {
        return Floats.checkedCast(getParameter(index));
    }

    @Override
    public float[] getFloatVector(final int index) {
        return Floats.checkedCastVector(getParameter(index));
    }

    @Override
    public float[][] getFloatMatrix(final int index) {
        return Floats.checkedCastMatrix(getParameter(index));
    }

    @Override
    public double getDouble(final int index) {
        return Doubles.checkedCast(getParameter(index));
    }

    @Override
    public double[] getDoubleVector(final int index) {
        return Doubles.checkedCastVector(getParameter(index));
    }

    @Override
    public double[][] getDoubleMatrix(final int index) {
        return Doubles.checkedCastMatrix(getParameter(index));
    }

    @Override
    public short getShort(final int index) {
        return Shorts.checkedCast(getParameter(index));
    }

    @Override
    public short[] getShortVector(final int index) {
        return Shorts.checkedCastVector(getParameter(index));
    }

    @Override
    public short[][] getShortMatrix(final int index) {
        return Shorts.checkedCastMatrix(getParameter(index));
    }

    @Override
    public int getInteger(final int index) {
        return Integers.checkedCast(getParameter(index));
    }

    @Override
    public int[] getIntegerVector(final int index) {
        return Integers.checkedCastVector(getParameter(index));
    }

    @Override
    public int[][] getIntegerMatrix(final int index) {
        return Integers.checkedCastMatrix(getParameter(index));
    }

    @Override
    public long getLong(final int index) {
        return Longs.checkedCast(getParameter(index));
    }

    @Override
    public long[] getLongVector(final int index) {
        return Longs.checkedCastVector(getParameter(index));
    }

    @Override
    public long[][] getLongMatrix(final int index) {
        return Longs.checkedCastMatrix(getParameter(index));
    }

    @Override
    public boolean getBoolean(final int index) {
        return Booleans.checkedCast(getParameter(index));
    }

    @Override
    public boolean[] getBooleanVector(final int index) {
        return Booleans.checkedCastVector(getParameter(index));
    }

    @Override
    public boolean[][] getBooleanMatrix(final int index) {
        return Booleans.checkedCastMatrix(getParameter(index));
    }

    @Override
    public void close() {
        parameters = null;
    }

}
