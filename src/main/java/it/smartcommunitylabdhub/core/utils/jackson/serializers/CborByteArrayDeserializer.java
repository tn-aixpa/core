package it.smartcommunitylabdhub.core.utils.jackson.serializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CborByteArrayDeserializer extends JsonDeserializer<Map<String, Object>> {

    @Override
    public Map<String, Object> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        // Custom logic to deserialize the CBOR-encoded "metadata" field
        byte[] cborData = p.getBinaryValue();
        Map<String, Object> result = new HashMap<>();
        result.put("metadata", cborData);
        return result;
    }
}
