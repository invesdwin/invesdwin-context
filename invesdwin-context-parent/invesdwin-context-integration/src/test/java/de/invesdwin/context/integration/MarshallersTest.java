package de.invesdwin.context.integration;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.ThreadSafe;

import org.junit.Test;

import com.fasterxml.jackson.core.type.TypeReference;

import de.invesdwin.context.test.ATest;
import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.bean.AValueObject;

@ThreadSafe
public class MarshallersTest extends ATest {

    @Test
    public void testJson() {
        final List<SomeValueObject<String>> list = new ArrayList<SomeValueObject<String>>();
        for (int i = 0; i < 5; i++) {
            final SomeValueObject<String> vo = new SomeValueObject<String>();
            vo.setValue(i + "a");
            list.add(vo);
        }

        final String jsonIncorrect = Marshallers.toJson(list);
        final String jsonGenericIncorrect = Marshallers.toJson(list);
        final TypeReference<List<SomeValueObject<String>>> correctType = new TypeReference<List<SomeValueObject<String>>>() {
        };
        final String jsonGenericCorrect = Marshallers.toJson(list);
        Assertions.assertThat(jsonGenericIncorrect).isEqualTo(jsonIncorrect);
        Assertions.assertThat(jsonGenericCorrect).isEqualTo(jsonIncorrect);
        log.info(jsonGenericCorrect);

        final List<SomeValueObject<String>> listUnmarshalled = Marshallers.fromJson(jsonGenericCorrect, correctType);
        Assertions.assertThat(listUnmarshalled).isEqualTo(list);
        log.info("%s", listUnmarshalled);
    }

    public static class SomeValueObject<T> extends AValueObject {
        private static final long serialVersionUID = 1L;
        private T value;
        @SuppressWarnings("unused")
        private final String asdf = "asdf";

        public T getValue() {
            return value;
        }

        public void setValue(final T wert) {
            this.value = wert;
        }

    }

}
