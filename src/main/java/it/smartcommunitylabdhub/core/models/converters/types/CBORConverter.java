package it.smartcommunitylabdhub.core.models.converters.types;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import it.smartcommunitylabdhub.core.annotations.common.ConverterType;
import it.smartcommunitylabdhub.core.exceptions.CustomException;
import it.smartcommunitylabdhub.core.models.converters.interfaces.Converter;
import it.smartcommunitylabdhub.core.utils.jackson.JacksonMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@ConverterType(type = "cbor")
public class CBORConverter implements Converter<Map<String, Object>, byte[]> {

    @Override
    public byte[] convert(Map<String, Object> map) throws CustomException {
        try {
            return JacksonMapper.CBOR_OBJECT_MAPPER.writeValueAsBytes(map);
        } catch (JsonProcessingException e) {
            throw new CustomException(null, e);
        }
    }

    @Override
    public Map<String, Object> reverseConvert(byte[] cborBytes) throws CustomException {
        try {
            if (cborBytes == null) {
                return new HashMap<>();
            }
            return JacksonMapper.CBOR_OBJECT_MAPPER.readValue(cborBytes, new TypeReference<>() {
            });
        } catch (IOException e) {
            throw new CustomException(null, e);
        }
    }
}
