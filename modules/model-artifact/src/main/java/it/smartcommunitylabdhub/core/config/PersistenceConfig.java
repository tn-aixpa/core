package it.smartcommunitylabdhub.core.config;

import it.smartcommunitylabdhub.artifacts.persistence.ArtifactEntity;
import it.smartcommunitylabdhub.artifacts.persistence.ArtifactRepository;
import it.smartcommunitylabdhub.commons.models.artifact.Artifact;
import it.smartcommunitylabdhub.core.models.base.BaseEntityServiceImpl;
import it.smartcommunitylabdhub.core.models.service.EntityService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.converter.Converter;

@Configuration
@Order(2)
public class PersistenceConfig {

    @Bean
    public EntityService<Artifact> artifactEntityService(
        ArtifactRepository repository,
        Converter<Artifact, ArtifactEntity> entityBuilder,
        Converter<ArtifactEntity, Artifact> dtoBuilder
    ) {
        return new BaseEntityServiceImpl<>(repository, entityBuilder, dtoBuilder);
    }
}
