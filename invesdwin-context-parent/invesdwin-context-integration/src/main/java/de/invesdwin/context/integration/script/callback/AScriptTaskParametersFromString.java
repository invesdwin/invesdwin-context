package de.invesdwin.context.integration.script.callback;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.math.Booleans;
import de.invesdwin.util.math.Bytes;
import de.invesdwin.util.math.Characters;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.math.Longs;
import de.invesdwin.util.math.Shorts;

@Immutable
public abstract class AScriptTaskParametersFromString implements IScriptTaskParameters {

    @Override
    public char getCharacter(final int index) {
        final String str = getString(index);
        if (str == null) {
            return Characters.DEFAULT_MISSING_VALUE;
        } else {
            return Characters.checkedCast(str);
        }
    }

    @Override
    public char[] getCharacterVector(final int index) {
        final String[] strs = getStringVector(index);
        if (strs == null) {
            return null;
        }
        final char[] values = new char[strs.length];
        for (int i = 0; i < strs.length; i++) {
            final String str = strs[i];
            if (str == null) {
                values[i] = Characters.DEFAULT_MISSING_VALUE;
            } else {
                values[i] = Characters.checkedCast(str);
            }
        }
        return values;
    }

    @Override
    public char[][] getCharacterMatrix(final int index) {
        final String[][] strsMatrix = getStringMatrix(index);
        if (strsMatrix == null) {
            return null;
        }
        final char[][] valuesMatrix = new char[strsMatrix.length][];
        for (int i = 0; i < strsMatrix.length; i++) {
            final String[] strs = strsMatrix[i];
            final char[] values = new char[strs.length];
            valuesMatrix[i] = values;
            for (int j = 0; j < strs.length; j++) {
                final String str = strs[j];
                if (str == null) {
                    values[j] = Characters.DEFAULT_MISSING_VALUE;
                } else {
                    values[j] = Characters.checkedCast(str);
                }
            }
        }
        return valuesMatrix;
    }

    @Override
    public boolean getBoolean(final int index) {
        final String str = getString(index);
        if (str == null) {
            return Booleans.DEFAULT_MISSING_VALUE;
        } else {
            return Boolean.parseBoolean(str);
        }
    }

    @Override
    public boolean[] getBooleanVector(final int index) {
        final String[] strs = getStringVector(index);
        if (strs == null) {
            return null;
        }
        final boolean[] values = new boolean[strs.length];
        for (int i = 0; i < strs.length; i++) {
            final String str = strs[i];
            if (str == null) {
                values[i] = Booleans.DEFAULT_MISSING_VALUE;
            } else {
                values[i] = Boolean.parseBoolean(str);
            }
        }
        return values;
    }

    @Override
    public boolean[][] getBooleanMatrix(final int index) {
        final String[][] strsMatrix = getStringMatrix(index);
        if (strsMatrix == null) {
            return null;
        }
        final boolean[][] valuesMatrix = new boolean[strsMatrix.length][];
        for (int i = 0; i < strsMatrix.length; i++) {
            final String[] strs = strsMatrix[i];
            final boolean[] values = new boolean[strs.length];
            valuesMatrix[i] = values;
            for (int j = 0; j < strs.length; j++) {
                final String str = strs[j];
                if (str == null) {
                    values[j] = Booleans.DEFAULT_MISSING_VALUE;
                } else {
                    values[j] = Boolean.parseBoolean(str);
                }
            }
        }
        return valuesMatrix;
    }

    @Override
    public byte getByte(final int index) {
        final String str = getString(index);
        if (str == null) {
            return Bytes.DEFAULT_MISSING_VALUE;
        } else {
            return Byte.parseByte(str);
        }
    }

    @Override
    public byte[] getByteVector(final int index) {
        final String[] strs = getStringVector(index);
        if (strs == null) {
            return null;
        }
        final byte[] values = new byte[strs.length];
        for (int i = 0; i < strs.length; i++) {
            final String str = strs[i];
            if (str == null) {
                values[i] = Bytes.DEFAULT_MISSING_VALUE;
            } else {
                values[i] = Byte.parseByte(str);
            }
        }
        return values;
    }

    @Override
    public byte[][] getByteMatrix(final int index) {
        final String[][] strsMatrix = getStringMatrix(index);
        if (strsMatrix == null) {
            return null;
        }
        final byte[][] valuesMatrix = new byte[strsMatrix.length][];
        for (int i = 0; i < strsMatrix.length; i++) {
            final String[] strs = strsMatrix[i];
            final byte[] values = new byte[strs.length];
            valuesMatrix[i] = values;
            for (int j = 0; j < strs.length; j++) {
                final String str = strs[j];
                if (str == null) {
                    values[j] = Bytes.DEFAULT_MISSING_VALUE;
                } else {
                    values[j] = Byte.parseByte(str);
                }
            }
        }
        return valuesMatrix;
    }

    @Override
    public short getShort(final int index) {
        final String str = getString(index);
        if (str == null) {
            return Shorts.DEFAULT_MISSING_VALUE;
        } else {
            return Short.parseShort(str);
        }
    }

    @Override
    public short[] getShortVector(final int index) {
        final String[] strs = getStringVector(index);
        if (strs == null) {
            return null;
        }
        final short[] values = new short[strs.length];
        for (int i = 0; i < strs.length; i++) {
            final String str = strs[i];
            if (str == null) {
                values[i] = Shorts.DEFAULT_MISSING_VALUE;
            } else {
                values[i] = Short.parseShort(str);
            }
        }
        return values;
    }

    @Override
    public short[][] getShortMatrix(final int index) {
        final String[][] strsMatrix = getStringMatrix(index);
        if (strsMatrix == null) {
            return null;
        }
        final short[][] valuesMatrix = new short[strsMatrix.length][];
        for (int i = 0; i < strsMatrix.length; i++) {
            final String[] strs = strsMatrix[i];
            final short[] values = new short[strs.length];
            valuesMatrix[i] = values;
            for (int j = 0; j < strs.length; j++) {
                final String str = strs[j];
                if (str == null) {
                    values[j] = Shorts.DEFAULT_MISSING_VALUE;
                } else {
                    values[j] = Short.parseShort(str);
                }
            }
        }
        return valuesMatrix;
    }

    @Override
    public int getInteger(final int index) {
        final String str = getString(index);
        if (str == null) {
            return Integers.DEFAULT_MISSING_VALUE;
        } else {
            return Integer.parseInt(str);
        }
    }

    @Override
    public int[] getIntegerVector(final int index) {
        final String[] strs = getStringVector(index);
        if (strs == null) {
            return null;
        }
        final int[] values = new int[strs.length];
        for (int i = 0; i < strs.length; i++) {
            final String str = strs[i];
            if (str == null) {
                values[i] = Integers.DEFAULT_MISSING_VALUE;
            } else {
                values[i] = Integer.parseInt(str);
            }
        }
        return values;
    }

    @Override
    public int[][] getIntegerMatrix(final int index) {
        final String[][] strsMatrix = getStringMatrix(index);
        if (strsMatrix == null) {
            return null;
        }
        final int[][] valuesMatrix = new int[strsMatrix.length][];
        for (int i = 0; i < strsMatrix.length; i++) {
            final String[] strs = strsMatrix[i];
            final int[] values = new int[strs.length];
            valuesMatrix[i] = values;
            for (int j = 0; j < strs.length; j++) {
                final String str = strs[j];
                if (str == null) {
                    values[j] = Integers.DEFAULT_MISSING_VALUE;
                } else {
                    values[j] = Integer.parseInt(str);
                }
            }
        }
        return valuesMatrix;
    }

    @Override
    public long getLong(final int index) {
        final String str = getString(index);
        if (str == null) {
            return Longs.DEFAULT_MISSING_VALUE;
        } else {
            return Long.parseLong(str);
        }
    }

    @Override
    public long[] getLongVector(final int index) {
        final String[] strs = getStringVector(index);
        if (strs == null) {
            return null;
        }
        final long[] values = new long[strs.length];
        for (int i = 0; i < strs.length; i++) {
            final String str = strs[i];
            if (str == null) {
                values[i] = Longs.DEFAULT_MISSING_VALUE;
            } else {
                values[i] = Long.parseLong(str);
            }
        }
        return values;
    }

    @Override
    public long[][] getLongMatrix(final int index) {
        final String[][] strsMatrix = getStringMatrix(index);
        if (strsMatrix == null) {
            return null;
        }
        final long[][] valuesMatrix = new long[strsMatrix.length][];
        for (int i = 0; i < strsMatrix.length; i++) {
            final String[] strs = strsMatrix[i];
            final long[] values = new long[strs.length];
            valuesMatrix[i] = values;
            for (int j = 0; j < strs.length; j++) {
                final String str = strs[j];
                if (str == null) {
                    values[j] = Longs.DEFAULT_MISSING_VALUE;
                } else {
                    values[j] = Long.parseLong(str);
                }
            }
        }
        return valuesMatrix;
    }

    @Override
    public float getFloat(final int index) {
        final String str = getString(index);
        if (str == null) {
            return Float.NaN;
        } else {
            return Float.parseFloat(str);
        }
    }

    @Override
    public float[] getFloatVector(final int index) {
        final String[] strs = getStringVector(index);
        if (strs == null) {
            return null;
        }
        final float[] values = new float[strs.length];
        for (int i = 0; i < strs.length; i++) {
            final String str = strs[i];
            if (str == null) {
                values[i] = Float.NaN;
            } else {
                values[i] = Float.parseFloat(str);
            }
        }
        return values;
    }

    @Override
    public float[][] getFloatMatrix(final int index) {
        final String[][] strsMatrix = getStringMatrix(index);
        if (strsMatrix == null) {
            return null;
        }
        final float[][] valuesMatrix = new float[strsMatrix.length][];
        for (int i = 0; i < strsMatrix.length; i++) {
            final String[] strs = strsMatrix[i];
            final float[] values = new float[strs.length];
            valuesMatrix[i] = values;
            for (int j = 0; j < strs.length; j++) {
                final String str = strs[j];
                if (str == null) {
                    values[j] = Float.NaN;
                } else {
                    values[j] = Float.parseFloat(str);
                }
            }
        }
        return valuesMatrix;
    }

    @Override
    public double getDouble(final int index) {
        final String str = getString(index);
        return parseDouble(str);
    }

    @Override
    public double[] getDoubleVector(final int index) {
        final String[] strs = getStringVector(index);
        if (strs == null) {
            return null;
        }
        final double[] values = new double[strs.length];
        for (int i = 0; i < strs.length; i++) {
            final String str = strs[i];
            values[i] = parseDouble(str);
        }
        return values;
    }

    @Override
    public double[][] getDoubleMatrix(final int index) {
        final String[][] strsMatrix = getStringMatrix(index);
        if (strsMatrix == null) {
            return null;
        }
        final double[][] valuesMatrix = new double[strsMatrix.length][];
        for (int i = 0; i < strsMatrix.length; i++) {
            final String[] strs = strsMatrix[i];
            final double[] values = new double[strs.length];
            valuesMatrix[i] = values;
            for (int j = 0; j < strs.length; j++) {
                final String str = strs[j];
                values[j] = parseDouble(str);
            }
        }
        return valuesMatrix;
    }

    protected double parseDouble(final String str) {
        if (str == null) {
            return Double.NaN;
        } else if ("nan".equalsIgnoreCase(str)) {
            return Double.NaN;
        } else {
            return Double.parseDouble(str);
        }
    }

}
