package de.invesdwin.context.integration.csv;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Properties;

import javax.annotation.concurrent.Immutable;

import org.springframework.batch.item.file.transform.FieldSet;

import de.invesdwin.util.error.Throwables;
import de.invesdwin.util.lang.string.Strings;

@Immutable
public final class EmptyFieldSet implements FieldSet {

    public static final EmptyFieldSet INSTANCE = new EmptyFieldSet();

    private EmptyFieldSet() {}

    @Override
    public String[] getNames() {
        return Strings.EMPTY_ARRAY;
    }

    @Override
    public boolean hasNames() {
        return false;
    }

    @Override
    public String[] getValues() {
        return Strings.EMPTY_ARRAY;
    }

    @Override
    public String readString(final int index) {
        throw Throwables.newIndexOutOfBoundsException(index);
    }

    @Override
    public String readString(final String name) {
        throw new IllegalArgumentException("Undefined: " + name);
    }

    @Override
    public String readRawString(final int index) {
        throw Throwables.newIndexOutOfBoundsException(index);
    }

    @Override
    public String readRawString(final String name) {
        throw new IllegalArgumentException("Undefined");
    }

    @Override
    public boolean readBoolean(final int index) {
        throw Throwables.newIndexOutOfBoundsException(index);
    }

    @Override
    public boolean readBoolean(final String name) {
        throw new IllegalArgumentException("Undefined: " + name);
    }

    @Override
    public boolean readBoolean(final int index, final String trueValue) {
        throw Throwables.newIndexOutOfBoundsException(index);
    }

    @Override
    public boolean readBoolean(final String name, final String trueValue) {
        throw new IllegalArgumentException("Undefined: " + name);
    }

    @Override
    public char readChar(final int index) {
        throw Throwables.newIndexOutOfBoundsException(index);
    }

    @Override
    public char readChar(final String name) {
        throw new IllegalArgumentException("Undefined: " + name);
    }

    @Override
    public byte readByte(final int index) {
        throw Throwables.newIndexOutOfBoundsException(index);
    }

    @Override
    public byte readByte(final String name) {
        throw new IllegalArgumentException("Undefined: " + name);
    }

    @Override
    public short readShort(final int index) {
        throw Throwables.newIndexOutOfBoundsException(index);
    }

    @Override
    public short readShort(final String name) {
        throw new IllegalArgumentException("Undefined: " + name);
    }

    @Override
    public int readInt(final int index) {
        throw Throwables.newIndexOutOfBoundsException(index);
    }

    @Override
    public int readInt(final String name) {
        throw new IllegalArgumentException("Undefined: " + name);
    }

    @Override
    public int readInt(final int index, final int defaultValue) {
        throw Throwables.newIndexOutOfBoundsException(index);
    }

    @Override
    public int readInt(final String name, final int defaultValue) {
        throw new IllegalArgumentException("Undefined: " + name);
    }

    @Override
    public long readLong(final int index) {
        throw Throwables.newIndexOutOfBoundsException(index);
    }

    @Override
    public long readLong(final String name) {
        throw new IllegalArgumentException("Undefined: " + name);
    }

    @Override
    public long readLong(final int index, final long defaultValue) {
        throw Throwables.newIndexOutOfBoundsException(index);
    }

    @Override
    public long readLong(final String name, final long defaultValue) {
        throw new IllegalArgumentException("Undefined: " + name);
    }

    @Override
    public float readFloat(final int index) {
        throw Throwables.newIndexOutOfBoundsException(index);
    }

    @Override
    public float readFloat(final String name) {
        throw new IllegalArgumentException("Undefined: " + name);
    }

    @Override
    public double readDouble(final int index) {
        throw Throwables.newIndexOutOfBoundsException(index);
    }

    @Override
    public double readDouble(final String name) {
        throw new IllegalArgumentException("Undefined: " + name);
    }

    @Override
    public BigDecimal readBigDecimal(final int index) {
        throw Throwables.newIndexOutOfBoundsException(index);
    }

    @Override
    public BigDecimal readBigDecimal(final String name) {
        throw new IllegalArgumentException("Undefined: " + name);
    }

    @Override
    public BigDecimal readBigDecimal(final int index, final BigDecimal defaultValue) {
        throw Throwables.newIndexOutOfBoundsException(index);
    }

    @Override
    public BigDecimal readBigDecimal(final String name, final BigDecimal defaultValue) {
        throw new IllegalArgumentException("Undefined: " + name);
    }

    @Override
    public Date readDate(final int index) {
        throw Throwables.newIndexOutOfBoundsException(index);
    }

    @Override
    public Date readDate(final String name) {
        throw new IllegalArgumentException("Undefined: " + name);
    }

    @Override
    public Date readDate(final int index, final Date defaultValue) {
        throw Throwables.newIndexOutOfBoundsException(index);
    }

    @Override
    public Date readDate(final String name, final Date defaultValue) {
        throw new IllegalArgumentException("Undefined: " + name);
    }

    @Override
    public Date readDate(final int index, final String pattern) {
        throw Throwables.newIndexOutOfBoundsException(index);
    }

    @Override
    public Date readDate(final String name, final String pattern) {
        throw new IllegalArgumentException("Undefined: " + name);
    }

    @Override
    public Date readDate(final int index, final String pattern, final Date defaultValue) {
        throw Throwables.newIndexOutOfBoundsException(index);
    }

    @Override
    public Date readDate(final String name, final String pattern, final Date defaultValue) {
        throw new IllegalArgumentException("Undefined: " + name);
    }

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public Properties getProperties() {
        throw new IllegalStateException("Cannot create properties without meta data");
    }

}
