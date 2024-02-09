package it.smartcommunitylabdhub.commons.models.entities.dataitem.specs;

import java.util.Map;

import it.smartcommunitylabdhub.commons.models.base.specs.BaseSpec;
import it.smartcommunitylabdhub.commons.utils.jackson.JacksonMapper;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DataItemBaseSpec extends BaseSpec {

  private String key;
  private String path;

  @Override
  public void configure(Map<String, Object> data) {
    DataItemBaseSpec concreteSpec =
      JacksonMapper.CUSTOM_OBJECT_MAPPER.convertValue(
        data,
        DataItemBaseSpec.class
      );
    this.setKey(concreteSpec.getKey());
    this.setPath(concreteSpec.getPath());

    super.configure(data);

    this.setExtraSpecs(concreteSpec.getExtraSpecs());
  }
}
