package it.smartcommunitylabdhub.core.models.builders.function;

import it.smartcommunitylabdhub.core.models.builders.EntityFactory;
import it.smartcommunitylabdhub.core.models.converters.ConversionUtils;
import it.smartcommunitylabdhub.core.models.converters.types.MetadataConverter;
import it.smartcommunitylabdhub.core.models.entities.function.Function;
import it.smartcommunitylabdhub.core.models.entities.function.FunctionEntity;
import it.smartcommunitylabdhub.core.models.entities.function.metadata.FunctionMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class FunctionDTOBuilder {

    @Autowired
    MetadataConverter<FunctionMetadata> metadataConverter;

    public Function build(
            FunctionEntity function,
            boolean embeddable) {

        return EntityFactory.create(Function::new, function, builder -> builder
                .with(dto -> dto.setId(function.getId()))
                .with(dto -> dto.setKind(function.getKind()))
                .with(dto -> dto.setProject(function.getProject()))
                .with(dto -> dto.setName(function.getName()))
                .with(dto -> dto.setMetadata(Optional
                        .ofNullable(metadataConverter
                                .reverseByClass(function
                                                .getMetadata(),
                                        FunctionMetadata.class))
                        .orElseGet(FunctionMetadata::new)))


                .withIfElse(embeddable, (dto, condition) -> Optional
                        .ofNullable(function.getEmbedded())
                        .filter(embedded -> !condition
                                || (condition && embedded))
                        .ifPresent(embedded -> dto
                                .setSpec(ConversionUtils.reverse(
                                        function.getSpec(), "cbor"))))
                .withIfElse(embeddable, (dto, condition) -> Optional
                        .ofNullable(function.getEmbedded())
                        .filter(embedded -> !condition
                                || (condition && embedded))
                        .ifPresent(embedded -> dto
                                .setExtra(ConversionUtils.reverse(
                                        function.getExtra(),

                                        "cbor"))))
                .withIfElse(embeddable, (dto, condition) ->
                        Optional.ofNullable(function.getEmbedded())
                                .filter(embedded -> !condition
                                        || (condition && embedded))
                                .ifPresent(embedded -> dto
                                        .setStatus(ConversionUtils.reverse(
                                                function.getStatus(), "cbor")
                                        )
                                )

                )
                .with(dto -> dto.setEmbedded(function.getEmbedded()))
                .with(dto -> dto.setCreated(function.getCreated()))
                .with(dto -> dto.setUpdated(function.getUpdated()))

        );
    }
}
