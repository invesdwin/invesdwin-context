package de.invesdwin.context.integration.csv;

import java.io.InputStream;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.validation.BindException;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.collections.iterable.EmptyCloseableIterator;
import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.lang.string.Strings;

@Immutable
public class CsvVerification {

    public static final FieldSet INVALID_ROW = EmptyFieldSet.INSTANCE;

    private final List<String> headers;
    private final int columns;
    private final String separator;

    public CsvVerification(final String[] headers, final String separator) {
        this(Arrays.asList(headers), separator);
    }

    public CsvVerification(final List<String> headers, final String separator) {
        this.headers = headers;
        this.columns = headers.size();
        this.separator = separator;
    }

    /**
     * When parsing websites a column size of 1 may be bad because it will detect normal text lines as csv even if they
     * aren't. A trick might be to add another column to the request.
     */
    public CsvVerification(final int columns, final String separator) {
        this.headers = null;
        this.columns = columns;
        this.separator = separator;
    }

    public boolean isCsv(final String content) {
        if (content == null) {
            return false;
        }
        return isCsv(new ByteArrayResource(content.getBytes()));
    }

    public boolean isCsv(final InputStream in) {
        if (in == null) {
            return false;
        }
        return isCsv(new InputStreamResource(in));
    }

    public boolean isCsv(final Resource resource) {
        if (resource == null) {
            return false;
        }
        final FlatFileItemReader<FieldSet> items = newItemReader(resource);
        try {
            items.open(new ExecutionContext());
            FieldSet item = null;
            do {
                item = items.read();
                if (item != null && item != INVALID_ROW && item.getFieldCount() != columns) {
                    return false;
                }
            } while (item != null);
            return true;
        } catch (final Exception e) {
            return false;
        } finally {
            items.close();
        }
    }

    public String filterCsv(final String content) {
        if (content == null) {
            return null;
        }
        return filterCsv(new ByteArrayResource(content.getBytes()));
    }

    public String filterCsv(final InputStream in) {
        if (in == null) {
            return null;
        }
        return filterCsv(new InputStreamResource(in));
    }

    public String filterCsv(final Resource resource) {
        if (resource == null) {
            return null;
        }
        final FlatFileItemReader<FieldSet> items = newItemReader(resource);
        try {
            items.open(new ExecutionContext());
            StringBuilder sb = new StringBuilder();
            FieldSet item = null;
            do {
                item = items.read();
                if (item != null && item != INVALID_ROW && item.getFieldCount() == columns) {
                    for (final String column : item.getValues()) {
                        sb.append(column);
                        sb.append(separator);
                    }
                    sb = Strings.removeEnd(sb, 1);
                    sb.append("\n");
                }
            } while (item != null);
            return Strings.removeEnd(sb, 1).toString();
        } catch (final Exception e) {
            return null;
        } finally {
            items.close();
        }
    }

    public ICloseableIterator<FieldSet> iterator(final String content) {
        if (content == null) {
            return EmptyCloseableIterator.getInstance();
        }
        return iterator(new ByteArrayResource(content.getBytes()));
    }

    public ICloseableIterator<FieldSet> iterator(final InputStream in) {
        if (in == null) {
            return EmptyCloseableIterator.getInstance();
        }
        return iterator(new InputStreamResource(in));
    }

    public ICloseableIterator<FieldSet> iterator(final Resource resource) {
        if (resource == null) {
            return EmptyCloseableIterator.getInstance();
        }
        final FlatFileItemReader<FieldSet> items = newItemReader(resource);
        items.open(new ExecutionContext());
        return new ItemStreamReaderCloseableIterator<FieldSet>(items, INVALID_ROW);
    }

    private FlatFileItemReader<FieldSet> newItemReader(final Resource resource) {
        return new CsvItemReaderBuilder<FieldSet>().setResource(resource)
                .setDelimiter(separator)
                .setNames(headers)
                .setFieldSetMapper(new FieldSetMapper<FieldSet>() {

                    private boolean headerFound = headers == null;

                    @Override
                    public FieldSet mapFieldSet(final FieldSet fieldSet) throws BindException {
                        if (!headerFound) {
                            for (final String name : headers) {
                                Assertions.checkEquals(name, fieldSet.readString(name));
                            }
                            headerFound = true;
                            return INVALID_ROW;
                        }

                        return fieldSet;
                    }
                })
                .get();
    }

}
