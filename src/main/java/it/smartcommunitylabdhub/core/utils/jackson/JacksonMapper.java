package it.smartcommunitylabdhub.core.utils.jackson;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.cbor.CBORFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.smartcommunitylabdhub.core.models.base.specs.BaseSpec;
import it.smartcommunitylabdhub.core.models.base.specs.ConcreteSpecMixin;
import it.smartcommunitylabdhub.core.utils.jackson.mixins.MetadataMixin;

import java.util.HashMap;
import java.util.Map;

public class JacksonMapper {
    public static final ObjectMapper CUSTOM_OBJECT_MAPPER = new ObjectMapper();
    public static final TypeReference<HashMap<String, Object>> typeRef =
            new TypeReference<>() {
            };
    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    public static final ObjectMapper CBOR_OBJECT_MAPPER = new ObjectMapper(new CBORFactory());
    public static final ObjectMapper CBOR_QUEUE_MAPPER = new ObjectMapper(new CBORFactory());

    static {
        // Configure the ObjectMapper to not fail on unknown properties
        CUSTOM_OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        CUSTOM_OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        CUSTOM_OBJECT_MAPPER.registerModule(new JavaTimeModule());
        CUSTOM_OBJECT_MAPPER.addMixIn(BaseSpec.class, ConcreteSpecMixin.class); // Replace TaskTransformSpec with your concrete class
    }

    // Register mixin for cbor to map deserialization.
    static {
        CBOR_OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        CBOR_OBJECT_MAPPER.registerModule(new JavaTimeModule());
        CBOR_OBJECT_MAPPER.addMixIn(Map.class, MetadataMixin.class);
    }

    public static JavaType extractJavaType(Class<?> clazz) {
        // resolve generics type via subclass trick
        return CUSTOM_OBJECT_MAPPER.getTypeFactory().constructSimpleType(clazz, null);
    }
}
