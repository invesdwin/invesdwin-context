package de.invesdwin.context.integration.csv;

import javax.annotation.concurrent.Immutable;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.validation.BindException;

import de.invesdwin.util.lang.string.Strings;

@Immutable
public class CsvVerification {

    private final int columns;
    private final String separator;

    /**
     * When parsind websites a column size of 1 may be bad because it will detect normal text lines as csv even if they
     * arent. A trick might be to add another column to the request.
     */
    public CsvVerification(final int columns, final String separator) {
        this.columns = columns;
        this.separator = separator;
    }

    public boolean isCsv(final String content) {
        if (content == null) {
            return false;
        }
        final FlatFileItemReader<FieldSet> items = newItemReader(content);
        try {
            items.open(new ExecutionContext());
            FieldSet item = null;
            do {
                item = items.read();
                if (item != null && item.getFieldCount() != columns) {
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
        final FlatFileItemReader<FieldSet> items = newItemReader(content);
        try {
            items.open(new ExecutionContext());
            StringBuilder sb = new StringBuilder();
            FieldSet item = null;
            do {
                item = items.read();
                if (item != null && item.getFieldCount() == columns) {
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

    private FlatFileItemReader<FieldSet> newItemReader(final String content) {
        return new CsvItemReaderBuilder<FieldSet>().setResource(new ByteArrayResource(content.getBytes()))
                .setDelimiter(separator)
                .setFieldSetMapper(new FieldSetMapper<FieldSet>() {
                    @Override
                    public FieldSet mapFieldSet(final FieldSet fieldSet) throws BindException {
                        return fieldSet;
                    }
                })
                .get();
    }

}
