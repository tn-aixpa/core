package it.smartcommunitylabdhub.core.models.entities.dataitem.specs;

import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.commons.models.entities.dataitem.specs.DataItemBaseSpec;
import it.smartcommunitylabdhub.commons.utils.jackson.JacksonMapper;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SpecType(
  kind = "table",
  entity = EntityName.DATAITEM,
  factory = DataItemTableSpec.class
)
public class DataItemTableSpec extends DataItemBaseSpec {

  @Override
  public void configure(Map<String, Object> data) {
    DataItemTableSpec dataItemTableSpec =
      JacksonMapper.CUSTOM_OBJECT_MAPPER.convertValue(
        data,
        DataItemTableSpec.class
      );
    super.configure(data);

    this.setExtraSpecs(dataItemTableSpec.getExtraSpecs());
  }
}
