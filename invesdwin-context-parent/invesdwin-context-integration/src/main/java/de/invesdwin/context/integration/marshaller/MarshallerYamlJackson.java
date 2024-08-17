package de.invesdwin.context.integration.marshaller;

import java.io.IOException;

import javax.annotation.concurrent.ThreadSafe;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

/**
 * YAML parser can also parse JSON and it does not complain about nan lowercase value tokens like JSON parser:
 * https://stackoverflow.com/a/29858634/67492
 */
@ThreadSafe
public final class MarshallerYamlJackson {

    private static final MarshallerYamlJackson INSTANCE = new MarshallerYamlJackson();

    private final ObjectMapper yaml;

    private MarshallerYamlJackson() {
        yaml = newObjectMapper();
    }

    private ObjectMapper newObjectMapper() {
        final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        configureObjectMapper(mapper, false);
        return mapper;
    }

    public static void configureObjectMapper(final ObjectMapper mapper, final boolean multiline) {
        mapper.findAndRegisterModules();
        if (multiline) {
            mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        }
        // Uses Enum.toString() for serialization of an Enum
        mapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
        // Uses Enum.toString() for deserialization of an Enum
        mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
    }

    public static String toYaml(final Object object) {
        try {
            return INSTANCE.yaml.writeValueAsString(object);
        } catch (final JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T fromYaml(final String json, final TypeReference<T> type) {
        try {
            return INSTANCE.yaml.readValue(json, type);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T fromYaml(final String json, final Class<T> type) {
        try {
            return INSTANCE.yaml.readValue(json, type);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ObjectMapper getYamlMapper() {
        return yaml;
    }

    public static MarshallerYamlJackson getInstance() {
        return INSTANCE;
    }

}
