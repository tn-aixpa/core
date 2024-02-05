package it.smartcommunitylabdhub.core.models.converters.types;

import it.smartcommunitylabdhub.core.annotations.common.ConverterType;
import it.smartcommunitylabdhub.core.exceptions.CustomException;
import it.smartcommunitylabdhub.core.models.converters.interfaces.Converter;
import it.smartcommunitylabdhub.core.models.entities.secret.Secret;
import it.smartcommunitylabdhub.core.models.entities.secret.SecretEntity;

@ConverterType(type = "secret")
public class SecretConverter implements Converter<Secret, SecretEntity> {

    @Override
    public SecretEntity convert(Secret secretDTO) throws CustomException {
        return SecretEntity.builder()
                .id(secretDTO.getId())
                .kind(secretDTO.getKind())
                .name(secretDTO.getName())
                .project(secretDTO.getProject())
                .build();
    }

    @Override
    public Secret reverseConvert(SecretEntity secret) throws CustomException {
        return Secret.builder()
                .id(secret.getId())
                .kind(secret.getKind())
                .name(secret.getName())
                .project(secret.getProject())
                .build();
    }

}
