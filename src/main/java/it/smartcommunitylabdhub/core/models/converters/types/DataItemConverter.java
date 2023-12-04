package it.smartcommunitylabdhub.core.models.converters.types;

import it.smartcommunitylabdhub.core.annotations.common.ConverterType;
import it.smartcommunitylabdhub.core.exceptions.CustomException;
import it.smartcommunitylabdhub.core.models.converters.interfaces.Converter;
import it.smartcommunitylabdhub.core.models.entities.dataitem.DataItem;
import it.smartcommunitylabdhub.core.models.entities.dataitem.DataItemEntity;

@ConverterType(type = "dataitem")
public class DataItemConverter implements Converter<DataItem, DataItemEntity> {

    @Override
    public DataItemEntity convert(DataItem dataItemDTO) throws CustomException {
        return DataItemEntity.builder()
                .id(dataItemDTO.getId())
                .name(dataItemDTO.getName())
                .kind(dataItemDTO.getKind())
                .project(dataItemDTO.getProject())
                .build();
    }

    @Override
    public DataItem reverseConvert(DataItemEntity dataItem) throws CustomException {
        return DataItem.builder()
                .id(dataItem.getId())
                .name(dataItem.getName())
                .kind(dataItem.getKind())
                .project(dataItem.getProject())
                .build();
    }

}
