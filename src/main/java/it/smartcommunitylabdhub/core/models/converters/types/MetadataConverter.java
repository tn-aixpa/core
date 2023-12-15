package it.smartcommunitylabdhub.core.models.converters.types;

import com.fasterxml.jackson.core.JsonProcessingException;
import it.smartcommunitylabdhub.core.annotations.common.ConverterType;
import it.smartcommunitylabdhub.core.exceptions.CustomException;
import it.smartcommunitylabdhub.core.models.base.metadata.BaseMetadata;
import it.smartcommunitylabdhub.core.models.converters.interfaces.Converter;
import it.smartcommunitylabdhub.core.utils.jackson.JacksonMapper;

import java.io.IOException;
import java.util.Map;

@ConverterType(type = "metadata")
public class MetadataConverter<M extends BaseMetadata>
        extends AbstractConverter implements Converter<M, byte[]> {

    @Override
    public byte[] convert(M metadata) throws CustomException {
        try {
            // Convert the object of type M to a Map<String, Object>
            Map<String, Object> metadataMap = JacksonMapper.OBJECT_MAPPER.convertValue(metadata, Map.class);

            // Serialize the map to CBOR format
            return JacksonMapper.CBOR_OBJECT_MAPPER.writeValueAsBytes(metadataMap);
        } catch (JsonProcessingException e) {
            throw new CustomException(null, e);
        }
    }

    @Override
    public <C> C reverseByClass(byte[] cborBytes, Class<C> targetClass) throws CustomException {
        try {
            if (cborBytes == null) {
                return null;
            }

            // Deserialize CBOR bytes into a Map<String, Object>
            Map<String, Object> cborMap = JacksonMapper.CBOR_OBJECT_MAPPER.readValue(cborBytes, Map.class);

            // Convert the map to the target class
            return JacksonMapper.OBJECT_MAPPER.convertValue(cborMap, targetClass);
        } catch (IOException e) {
            throw new CustomException(null, e);
        }
    }

    @Override
    public M reverseConvert(byte[] cborBytes) throws CustomException {
        throw new UnsupportedOperationException("reverseConvert method not implemented." +
                "Call reverseByClass function instead passing the targetClass type.");
    }

}
