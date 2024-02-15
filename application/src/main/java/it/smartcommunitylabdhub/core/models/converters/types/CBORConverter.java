package it.smartcommunitylabdhub.core.models.converters.types;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import it.smartcommunitylabdhub.commons.annotations.common.ConverterType;
import it.smartcommunitylabdhub.commons.exceptions.CustomException;
import it.smartcommunitylabdhub.commons.jackson.JacksonMapper;
import it.smartcommunitylabdhub.core.models.converters.interfaces.Converter;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@ConverterType(type = "cbor")
public class CBORConverter implements Converter<Map<String, Serializable>, byte[]> {

    @Override
    public byte[] convert(Map<String, Serializable> map) throws CustomException {
        try {
            return JacksonMapper.CBOR_OBJECT_MAPPER.writeValueAsBytes(map);
        } catch (JsonProcessingException e) {
            throw new CustomException(null, e);
        }
    }

    @Override
    public Map<String, Serializable> reverseConvert(byte[] cborBytes) throws CustomException {
        try {
            if (cborBytes == null) {
                return new HashMap<>();
            }
            return JacksonMapper.CBOR_OBJECT_MAPPER.readValue(cborBytes, new TypeReference<>() {});
        } catch (IOException e) {
            throw new CustomException(null, e);
        }
    }
}
