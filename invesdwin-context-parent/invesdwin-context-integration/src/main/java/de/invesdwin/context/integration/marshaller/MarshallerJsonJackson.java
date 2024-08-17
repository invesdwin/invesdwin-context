package de.invesdwin.context.integration.marshaller;

import java.io.IOException;

import javax.annotation.concurrent.ThreadSafe;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.cfg.MapperBuilder;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.json.JsonMapper.Builder;

@ThreadSafe
public final class MarshallerJsonJackson {

    private static final MarshallerJsonJackson INSTANCE = new MarshallerJsonJackson();

    private final ObjectMapper json;
    private final ObjectMapper jsonMultiline;

    private MarshallerJsonJackson() {
        json = newObjectMapper(false);
        jsonMultiline = newObjectMapper(true);
    }

    private ObjectMapper newObjectMapper(final boolean multiline) {
        final Builder mapper = JsonMapper.builder();
        configureObjectMapper(mapper, multiline);
        return mapper.build();
    }

    public static void configureObjectMapper(final MapperBuilder<?, ?> mapper, final boolean multiline) {
        mapper.findAndAddModules();
        if (multiline) {
            mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        }
        // Uses Enum.toString() for serialization of an Enum
        mapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
        // Uses Enum.toString() for deserialization of an Enum
        mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
        //don't require quotes for field names
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        //allow NaN without quotes
        mapper.configure(JsonReadFeature.ALLOW_NON_NUMERIC_NUMBERS.mappedFeature(), true);
    }

    public static String toJson(final Object object, final boolean multiline) {
        if (multiline) {
            return toJsonMultiline(object);
        } else {
            return toJson(object);
        }
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

    public static <T> T fromJson(final String json, final Class<T> type) {
        try {
            return INSTANCE.json.readValue(json, type);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ObjectMapper getJsonMapper(final boolean multiline) {
        if (multiline) {
            return jsonMultiline;
        } else {
            return json;
        }
    }

    public static MarshallerJsonJackson getInstance() {
        return INSTANCE;
    }

}
