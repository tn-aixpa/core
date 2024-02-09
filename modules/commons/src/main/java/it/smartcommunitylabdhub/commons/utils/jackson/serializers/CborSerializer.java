package it.smartcommunitylabdhub.commons.utils.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import it.smartcommunitylabdhub.commons.utils.jackson.JacksonMapper;

import java.io.IOException;
import java.util.Map;

public class CborSerializer extends StdSerializer<byte[]> {

    public CborSerializer() {
        this(null);
    }

    public CborSerializer(Class<byte[]> t) {
        super(t);
    }

    @Override
    public void serialize(byte[] bytes, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        Map<String, Object> value = JacksonMapper.CBOR_OBJECT_MAPPER.readValue(bytes, new TypeReference<>() {
        });
        jsonGenerator.writeObject(value);
    }


}