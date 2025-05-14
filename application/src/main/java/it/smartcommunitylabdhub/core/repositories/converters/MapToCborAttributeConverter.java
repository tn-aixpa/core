package it.smartcommunitylabdhub.core.repositories.converters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.smartcommunitylabdhub.commons.jackson.JacksonMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component("cborMapConverter")
@Converter(autoApply = false)
public class MapToCborAttributeConverter implements AttributeConverter<Map<String, Serializable>, byte[]> {

    private static final ObjectMapper mapper = JacksonMapper.CBOR_OBJECT_MAPPER;
    private static final TypeReference<HashMap<String, Serializable>> typeRef = new TypeReference<
        HashMap<String, Serializable>
    >() {};

    @Override
    public byte[] convertToDatabaseColumn(Map<String, Serializable> map) {
        byte[] value = null;
        if (map != null) {
            try {
                value = mapper.writeValueAsBytes(map);
            } catch (JsonProcessingException e) {
                log.error("error converting map: {}", e.getMessage());
            }
        }

        return value;
    }

    @Override
    public Map<String, Serializable> convertToEntityAttribute(byte[] source) {
        Map<String, Serializable> value = null;

        if (source != null) {
            try {
                value = mapper.readValue(source, typeRef);
            } catch (IOException e) {
                log.error("error reading map from bytes: {}", e.getMessage());
            }
        }
        return value;
    }
}
