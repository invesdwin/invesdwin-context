package de.invesdwin.context.integration.marshaller;

import javax.annotation.concurrent.ThreadSafe;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.xml.transform.StringResult;
import org.springframework.xml.transform.StringSource;

import de.invesdwin.context.beans.init.MergedContext;
import de.invesdwin.util.assertions.Assertions;

@Configurable
@ThreadSafe
public final class MarshallerXmlJaxb implements ApplicationContextAware {

    private static final MarshallerXmlJaxb INSTANCE = new MarshallerXmlJaxb();

    private Jaxb2Marshaller jaxb;

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) {
        try {
            this.jaxb = applicationContext.getBean(Jaxb2Marshaller.class);
        } catch (final NoSuchBeanDefinitionException e) {//SUPPRESS CHECKSTYLE empty block
            //ignore
        }
    }

    private static void checkJaxbConfigured() {
        MergedContext.assertBootstrapFinished();
        Assertions.assertThat(INSTANCE.jaxb)
                .as("No %s defined, thus there is no marshaller for xml available! If desired please put an xsd into /META-INF/xsd/ and generate this for it.",
                        IMergedJaxbContextPath.class)
                .isNotNull();
    }

    public static String toXml(final Object object) {
        checkJaxbConfigured();
        final StringResult res = new StringResult();
        INSTANCE.jaxb.marshal(object, res);
        return res.toString();
    }

    @SuppressWarnings("unchecked")
    public static <T> T fromXml(final String xml) {
        checkJaxbConfigured();
        final StringSource ss = new StringSource(xml);
        return (T) INSTANCE.jaxb.unmarshal(ss);
    }

    public Jaxb2Marshaller getJaxb2Marshaller() {
        return jaxb;
    }

    public static MarshallerXmlJaxb getInstance() {
        return INSTANCE;
    }

}
