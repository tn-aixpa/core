package it.smartcommunitylabdhub.core.config;

import it.smartcommunitylabdhub.commons.models.model.Model;
import it.smartcommunitylabdhub.core.models.base.BaseEntityServiceImpl;
import it.smartcommunitylabdhub.core.models.service.EntityService;
import it.smartcommunitylabdhub.models.persistence.ModelEntity;
import it.smartcommunitylabdhub.models.persistence.ModelRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.converter.Converter;

@Configuration
@Order(2)
public class PersistenceConfig {

    @Bean
    public EntityService<Model> modelEntityService(
        ModelRepository repository,
        Converter<Model, ModelEntity> entityBuilder,
        Converter<ModelEntity, Model> dtoBuilder
    ) {
        return new BaseEntityServiceImpl<>(repository, entityBuilder, dtoBuilder);
    }
}
