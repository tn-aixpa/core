package it.smartcommunitylabdhub.core.utils.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.Map;

public class CborByteArraySerializer extends JsonSerializer<Map<String, Object>> {

    @Override
    public void serialize(Map<String, Object> value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        // Custom logic to serialize the "metadata" field as CBOR
        String clazz = value.keySet().iterator().next();
        byte[] cborData = (byte[]) ((Map<String, Object>) value.get(clazz)).get("metadata");
        gen.writeBinary(cborData);
    }
}
