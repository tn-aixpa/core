package it.smartcommunitylabdhub.core.models.converters.types;

import it.smartcommunitylabdhub.core.annotations.common.ConverterType;
import it.smartcommunitylabdhub.core.exceptions.CustomException;
import it.smartcommunitylabdhub.core.models.converters.interfaces.Converter;
import it.smartcommunitylabdhub.core.models.entities.artifact.Artifact;
import it.smartcommunitylabdhub.core.models.entities.artifact.ArtifactEntity;

@ConverterType(type = "artifact")
public class ArtifactConverter implements Converter<Artifact, ArtifactEntity> {

    @Override
    public ArtifactEntity convert(Artifact artifactDTO) throws CustomException {
        return ArtifactEntity.builder()
                .id(artifactDTO.getId())
                .name(artifactDTO.getName())
                .kind(artifactDTO.getKind())
                .project(artifactDTO.getProject())
                .build();
    }

    @Override
    public Artifact reverseConvert(ArtifactEntity artifact) throws CustomException {
        return Artifact.builder()
                .id(artifact.getId())
                .name(artifact.getName())
                .kind(artifact.getKind())
                .project(artifact.getProject())
                .build();
    }

}
