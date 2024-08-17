package de.invesdwin.context.integration.marshaller;

import java.io.IOException;

import javax.annotation.concurrent.ThreadSafe;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper.Builder;

@ThreadSafe
public final class MarshallerXmlJackson {

    private static final MarshallerXmlJackson INSTANCE = new MarshallerXmlJackson();

    private final XmlMapper xml;
    private final XmlMapper xmlMultiline;

    private MarshallerXmlJackson() {
        xml = newXmlMapper(false);
        xmlMultiline = newXmlMapper(true);
    }

    private XmlMapper newXmlMapper(final boolean multiline) {
        final Builder mapper = XmlMapper.builder();
        MarshallerJsonJackson.configureObjectMapper(mapper, multiline);
        return mapper.build();
    }

    public static String toXml(final Object object, final boolean multiline) {
        if (multiline) {
            return toXmlMultiline(object);
        } else {
            return toXml(object);
        }
    }

    public static String toXml(final Object object) {
        try {
            return INSTANCE.xml.writeValueAsString(object);
        } catch (final JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String toXmlMultiline(final Object object) {
        try {
            return INSTANCE.xmlMultiline.writeValueAsString(object);
        } catch (final JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T fromXml(final String xml, final TypeReference<T> type) {
        try {
            return INSTANCE.xml.readValue(xml, type);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T fromXml(final String xml, final Class<T> type) {
        try {
            return INSTANCE.xml.readValue(xml, type);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    public XmlMapper getXmlMapper(final boolean multiline) {
        if (multiline) {
            return xmlMultiline;
        } else {
            return xml;
        }
    }

    public static MarshallerXmlJackson getInstance() {
        return INSTANCE;
    }

}
