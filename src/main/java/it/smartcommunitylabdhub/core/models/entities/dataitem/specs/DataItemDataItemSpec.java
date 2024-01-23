package it.smartcommunitylabdhub.core.models.entities.dataitem.specs;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.smartcommunitylabdhub.core.annotations.common.SpecType;
import it.smartcommunitylabdhub.core.components.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.core.utils.jackson.JacksonMapper;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;


@Getter
@Setter
@SpecType(kind = "dataitem", entity = EntityName.DATAITEM, factory = DataItemDataItemSpec.class)
public class DataItemDataItemSpec extends DataItemBaseSpec {
    @JsonProperty("raw_code")
    private String rawCode;
    @JsonProperty("compiled_code")
    private String compiledCode;

    @Override
    public void configure(Map<String, Object> data) {

        DataItemDataItemSpec dataItemDataItemSpec = JacksonMapper.CUSTOM_OBJECT_MAPPER.convertValue(
                data, DataItemDataItemSpec.class);

        this.setRawCode(dataItemDataItemSpec.getRawCode());
        this.setCompiledCode(dataItemDataItemSpec.getCompiledCode());
        super.configure(data);

        this.setExtraSpecs(dataItemDataItemSpec.getExtraSpecs());
    }
}
