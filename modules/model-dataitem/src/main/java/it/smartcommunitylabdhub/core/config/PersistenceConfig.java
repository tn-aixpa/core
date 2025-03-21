package it.smartcommunitylabdhub.core.config;

import it.smartcommunitylabdhub.commons.models.dataitem.DataItem;
import it.smartcommunitylabdhub.core.models.base.BaseEntityServiceImpl;
import it.smartcommunitylabdhub.core.models.service.EntityService;
import it.smartcommunitylabdhub.dataitems.persistence.DataItemEntity;
import it.smartcommunitylabdhub.dataitems.persistence.DataItemRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.converter.Converter;

@Configuration
@Order(2)
public class PersistenceConfig {

    @Bean
    public EntityService<DataItem> dataItemEntityService(
        DataItemRepository repository,
        Converter<DataItem, DataItemEntity> entityBuilder,
        Converter<DataItemEntity, DataItem> dtoBuilder
    ) {
        return new BaseEntityServiceImpl<>(repository, entityBuilder, dtoBuilder);
    }
}
