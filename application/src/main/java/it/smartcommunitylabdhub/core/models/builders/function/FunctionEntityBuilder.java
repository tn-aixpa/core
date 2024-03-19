package it.smartcommunitylabdhub.core.models.builders.function;

import it.smartcommunitylabdhub.commons.accessors.fields.StatusFieldAccessor;
import it.smartcommunitylabdhub.commons.models.entities.function.Function;
import it.smartcommunitylabdhub.commons.models.entities.function.FunctionMetadata;
import it.smartcommunitylabdhub.commons.models.enums.State;
import it.smartcommunitylabdhub.core.models.converters.types.CBORConverter;
import it.smartcommunitylabdhub.core.models.entities.function.FunctionEntity;
import java.time.ZoneOffset;
import java.util.Date;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class FunctionEntityBuilder implements Converter<Function, FunctionEntity> {

    private final CBORConverter cborConverter;

    public FunctionEntityBuilder(CBORConverter cborConverter) {
        this.cborConverter = cborConverter;
    }

    /**
     * Build a function from a functionDTO and store extra values as f cbor
     * <p>
     *
     * @param dto the functionDTO that need to be stored
     * @return Function
     */
    public FunctionEntity build(Function dto) {
        // Retrieve field accessor
        StatusFieldAccessor statusFieldAccessor = StatusFieldAccessor.with(dto.getStatus());
        FunctionMetadata metadata = new FunctionMetadata();
        metadata.configure(dto.getMetadata());

        return FunctionEntity
            .builder()
            .id(dto.getId())
            .name(dto.getName())
            .kind(dto.getKind())
            .project(dto.getProject())
            .metadata(cborConverter.convert(dto.getMetadata()))
            .spec(cborConverter.convert(dto.getSpec()))
            .status(cborConverter.convert(dto.getStatus()))
            .extra(cborConverter.convert(dto.getExtra()))
            .state(
                // Store status if not present
                statusFieldAccessor.getState() == null ? State.CREATED : State.valueOf(statusFieldAccessor.getState())
            )
            // Metadata Extraction
            .embedded(metadata.getEmbedded() == null ? Boolean.FALSE : metadata.getEmbedded())
            .created(
                metadata.getCreated() != null
                    ? Date.from(metadata.getCreated().atZoneSameInstant(ZoneOffset.UTC).toInstant())
                    : null
            )
            .updated(
                metadata.getUpdated() != null
                    ? Date.from(metadata.getUpdated().atZoneSameInstant(ZoneOffset.UTC).toInstant())
                    : null
            )
            .build();
    }

    @Override
    public FunctionEntity convert(Function source) {
        return build(source);
    }
}
