package it.smartcommunitylabdhub.commons.jackson;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.cbor.CBORFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.smartcommunitylabdhub.commons.jackson.mixins.ConcreteSpecMixin;
import it.smartcommunitylabdhub.commons.models.base.BaseSpec;
import java.util.HashMap;

//TODO refactor into a factory ( create method to retrieve all objects mapper)
public class JacksonMapper {

    public static final ObjectMapper CUSTOM_OBJECT_MAPPER = new ObjectMapper();
    public static final TypeReference<HashMap<String, Object>> typeRef = new TypeReference<>() {};
    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    public static final ObjectMapper CBOR_OBJECT_MAPPER = new ObjectMapper(new CBORFactory());

    static {
        OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OBJECT_MAPPER.registerModule(new JavaTimeModule());
    }

    static {
        // Configure the ObjectMapper to not fail on unknown properties
        CUSTOM_OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        CUSTOM_OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        CUSTOM_OBJECT_MAPPER.setSerializationInclusion(Include.NON_EMPTY);
        CUSTOM_OBJECT_MAPPER.registerModule(new JavaTimeModule());
        CUSTOM_OBJECT_MAPPER.addMixIn(BaseSpec.class, ConcreteSpecMixin.class);
    }

    // Register mixin for cbor to map deserialization.
    static {
        CBOR_OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        CBOR_OBJECT_MAPPER.registerModule(new JavaTimeModule());
    }

    public static JavaType extractJavaType(Class<?> clazz) {
        // resolve generics type via subclass trick
        return CUSTOM_OBJECT_MAPPER.getTypeFactory().constructSimpleType(clazz, null);
    }

    public static ObjectWriter getCustomObjectMapperWrite() {
        return CUSTOM_OBJECT_MAPPER.writer();
    }

    public static ObjectReader getCustomObjectMapperReader() {
        return CUSTOM_OBJECT_MAPPER.reader();
    }
}
