package it.smartcommunitylabdhub.core.models.entities.dataitem.specs;


import it.smartcommunitylabdhub.core.annotations.common.SpecType;
import it.smartcommunitylabdhub.core.components.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.core.utils.jackson.JacksonMapper;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@SpecType(kind = "table", entity = EntityName.DATAITEM, factory = DataItemTableSpec.class)
public class DataItemTableSpec extends DataItemBaseSpec {
    @Override
    public void configure(Map<String, Object> data) {
        DataItemTableSpec dataItemTableSpec = JacksonMapper.CUSTOM_OBJECT_MAPPER.convertValue(
                data, DataItemTableSpec.class);
        super.configure(data);

        this.setExtraSpecs(dataItemTableSpec.getExtraSpecs());
    }
}
