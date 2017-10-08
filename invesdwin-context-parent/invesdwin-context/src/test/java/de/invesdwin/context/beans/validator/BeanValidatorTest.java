package de.invesdwin.context.beans.validator;

import javax.annotation.concurrent.ThreadSafe;
import javax.inject.Inject;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.junit.jupiter.api.Test;

import de.invesdwin.context.test.ATest;
import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.bean.AValueObject;

@ThreadSafe
public class BeanValidatorTest extends ATest {

    @Inject
    private BeanValidator clazz;
    @Inject
    private javax.validation.Validator jsrInterface;
    @Inject
    private org.springframework.validation.Validator springInterface;

    @Test
    public void testInjection() {
        Assertions.assertThat(clazz).isNotNull();
        Assertions.assertThat(jsrInterface).isNotNull();
        Assertions.assertThat(springInterface).isNotNull();
        Assertions.assertThat(clazz).isSameAs(jsrInterface).isSameAs(springInterface);
    }

    @Test
    public void testValdateSuccess() {
        final ToBeVaditatedClass k = new ToBeVaditatedClass();
        k.setSomeValue("asd");
        BeanValidator.getInstance().validate(k);
    }

    @Test
    public void testValidateFailed() {
        final ToBeVaditatedClass innerInner = new ToBeVaditatedClass();
        final ToBeVaditatedClass inner = new ToBeVaditatedClass();
        inner.setInnerValidatableKlasse(innerInner);
        final ToBeVaditatedClass k = new ToBeVaditatedClass();
        k.setSomeValue("asdf");
        k.setInnerValidatableKlasse(inner);
        final ConstraintViolationException violation = BeanValidator.getInstance().validate(k);
        Assertions.assertThat(violation).isNotNull();
        Assertions.assertThat(violation.getConstraintViolations().size()).isEqualTo(2);
    }

    @SuppressWarnings("unused")
    private static class ToBeVaditatedClass extends AValueObject {

        @NotNull
        private String someValue;
        @Valid
        private ToBeVaditatedClass innerValidatableKlasse;

        public String getSomeValue() {
            return someValue;
        }

        public void setSomeValue(final String someValue) {
            this.someValue = someValue;
        }

        public ToBeVaditatedClass getInnerValidatableKlasse() {
            return innerValidatableKlasse;
        }

        public void setInnerValidatableKlasse(final ToBeVaditatedClass innerValidatableKlasse) {
            this.innerValidatableKlasse = innerValidatableKlasse;
        }

    }

}
