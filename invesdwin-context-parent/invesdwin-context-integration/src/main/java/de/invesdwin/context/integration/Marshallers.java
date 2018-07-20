package de.invesdwin.context.integration;

import java.io.IOException;

import javax.annotation.concurrent.ThreadSafe;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.xml.transform.StringResult;
import org.springframework.xml.transform.StringSource;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import de.invesdwin.context.beans.init.MergedContext;
import de.invesdwin.util.assertions.Assertions;

@Configurable
@ThreadSafe
public final class Marshallers implements ApplicationContextAware {

    private static final Marshallers INSTANCE = new Marshallers();

    private Jaxb2Marshaller jaxb;
    private final ObjectMapper json;
    private final ObjectMapper jsonMultiline;

    private Marshallers() {
        json = newObjectMapper(false);
        jsonMultiline = newObjectMapper(true);
    }

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) {
        try {
            this.jaxb = applicationContext.getBean(Jaxb2Marshaller.class);
        } catch (final NoSuchBeanDefinitionException e) {//SUPPRESS CHECKSTYLE empty block
            //ignore
        }
    }

    private ObjectMapper newObjectMapper(final boolean multiline) {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        if (multiline) {
            mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        }
        // Uses Enum.toString() for serialization of an Enum
        mapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
        // Uses Enum.toString() for deserialization of an Enum
        mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
        //don't require quotes for field names
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        return mapper;
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

    public static String toJson(final Object object) {
        try {
            return INSTANCE.json.writeValueAsString(object);
        } catch (final JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String toJsonMultiline(final Object object) {
        try {
            return INSTANCE.jsonMultiline.writeValueAsString(object);
        } catch (final JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T fromJson(final String json, final TypeReference<T> type) {
        try {
            return INSTANCE.json.readValue(json, type);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ObjectMapper getJsonObjectMapper(final boolean multiline) {
        if (multiline) {
            return jsonMultiline;
        } else {
            return json;
        }
    }

    public Jaxb2Marshaller getJaxb2Marshaller() {
        return jaxb;
    }

    public static Marshallers getInstance() {
        return INSTANCE;
    }

}
