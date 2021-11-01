package de.invesdwin.context.integration.csv;

import javax.annotation.concurrent.ThreadSafe;

import org.junit.Test;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.validation.BindException;

import de.invesdwin.context.test.ATest;
import de.invesdwin.util.assertions.Assertions;

@ThreadSafe
public class CsvItemReaderBuilderTest extends ATest {

    @Test
    public void testTokenize() throws Exception {
        final FlatFileItemReader<String[]> items = new CsvItemReaderBuilder<String[]>()
                .setResource(new ByteArrayResource(
                        ("\"O:RU-B12-0-3325.C\",\"O:RU-B\n12-0-3,325.C\",0,0.00,N/A,N/A,\"asd\"ê\n\",\"N/A\"\n"
                                + "\"O:EC-O12-1-325.CM\",\"O:EC-O12-1-325.CM\",0,0.00,N/A,N/A,\"\"<x\",\"N/A\"\n"
                                + "\"O:EC-X11-1-405.CM\",\"\"\"O:EC-X11-1-405.CM\",0,0.00,N/A,N/A,\"P#7\",\"N/A\"")
                                        .getBytes()))
                .setFieldSetMapper(new FieldSetMapper<String[]>() {
                    @Override
                    public String[] mapFieldSet(final FieldSet fieldSet) throws BindException {
                        return fieldSet.getValues();
                    }
                }).get();
        items.open(new ExecutionContext());
        for (int i = 0; i < 3; i++) {
            final String[] values = items.read();
            for (final String v : values) {
                log.info(v);
            }
            Assertions.assertThat(values).isNotNull();
            Assertions.assertThat(values.length).isEqualTo(8);
        }

        Assertions.assertThat(items.read()).isNull();
    }
}
