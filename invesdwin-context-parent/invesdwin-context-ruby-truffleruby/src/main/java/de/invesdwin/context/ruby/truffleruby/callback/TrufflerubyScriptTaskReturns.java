package de.invesdwin.context.ruby.truffleruby.callback;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.context.integration.script.callback.ObjectScriptTaskReturns;
import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.math.Integers;

@NotThreadSafe
public class TrufflerubyScriptTaskReturns extends ObjectScriptTaskReturns {

    @Override
    public void returnString(final String value) {
        if (value == null) {
            returnNull();
        } else {
            returnExpression("\"" + value + "\"");
        }
    }

    @Override
    public void returnStringVector(final String[] value) {
        if (value == null) {
            returnNull();
        } else {
            final StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < value.length; i++) {
                if (i > 0) {
                    sb.append(",");
                }
                final String v = value[i];
                if (v == null) {
                    sb.append("nil");
                } else {
                    sb.append("\"");
                    sb.append(v);
                    sb.append("\"");
                }
            }
            sb.append("]");
            returnExpression(sb.toString());
        }
    }

    @Override
    public void returnStringMatrix(final String[][] value) {
        if (value == null) {
            returnNull();
        } else if (value.length == 0 || value[0].length == 0) {
            returnEmptyMatrix(value.length);
        } else {
            final int rows = value.length;
            final int cols = value[0].length;
            final StringBuilder sb = new StringBuilder("[");
            for (int row = 0; row < rows; row++) {
                final String[] valueRow = value[row];
                Assertions.checkEquals(valueRow.length, cols);
                if (row > 0) {
                    sb.append(",");
                }
                sb.append("[");
                for (int col = 0; col < cols; col++) {
                    if (col > 0) {
                        sb.append(",");
                    }
                    final String v = valueRow[col];
                    if (v == null) {
                        sb.append("nil");
                    } else {
                        sb.append("\"");
                        sb.append(v);
                        sb.append("\"");
                    }
                }
                sb.append("]");
            }
            sb.append("]");
            returnExpression(sb.toString());
        }
    }

    public void returnEmptyMatrix(final int rows) {
        final StringBuilder sb = new StringBuilder("[");
        for (int row = 0; row < rows; row++) {
            if (row > 0) {
                sb.append(",");
            }
            sb.append("[]");
        }
        sb.append("]");
        returnExpression(sb.toString());
    }

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
