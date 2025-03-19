package it.smartcommunitylabdhub.core.models.converters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.smartcommunitylabdhub.commons.jackson.JacksonMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component("cborStringConverter")
@Converter(autoApply = false)
public class StringToCborAttributeConverter implements AttributeConverter<String, byte[]> {

    private static final ObjectMapper mapper = JacksonMapper.CBOR_OBJECT_MAPPER;

    @Override
    public byte[] convertToDatabaseColumn(String value) {
        byte[] bytes = null;
        if (value != null) {
            try {
                bytes = mapper.writeValueAsBytes(value);
            } catch (JsonProcessingException e) {
                log.error("error converting string: {}", e.getMessage());
            }
        }

        return bytes;
    }

    @Override
    public String convertToEntityAttribute(byte[] bytes) {
        String value = null;

        if (bytes != null) {
            try {
                value = mapper.readValue(bytes, String.class);
            } catch (IOException e) {
                log.error("error reading string from bytes: {}", e.getMessage());
            }
        }
        return value;
    }
}
