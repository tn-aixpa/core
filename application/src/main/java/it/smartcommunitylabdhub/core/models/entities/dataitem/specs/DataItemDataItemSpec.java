package it.smartcommunitylabdhub.core.models.entities.dataitem.specs;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.commons.models.entities.dataitem.specs.DataItemBaseSpec;
import it.smartcommunitylabdhub.commons.utils.jackson.JacksonMapper;
import it.smartcommunitylabdhub.core.models.entities.dataitem.specs.factories.DataItemDataItemSpecFactory;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SpecType(kind = "dataitem", entity = EntityName.DATAITEM)
public class DataItemDataItemSpec extends DataItemBaseSpec {

    @JsonProperty("raw_code")
    private String rawCode;

    @JsonProperty("compiled_code")
    private String compiledCode;

    @Override
    public void configure(Map<String, Object> data) {
        DataItemDataItemSpec dataItemDataItemSpec = JacksonMapper.CUSTOM_OBJECT_MAPPER.convertValue(
            data,
            DataItemDataItemSpec.class
        );

        this.setRawCode(dataItemDataItemSpec.getRawCode());
        this.setCompiledCode(dataItemDataItemSpec.getCompiledCode());
        super.configure(data);

        this.setExtraSpecs(dataItemDataItemSpec.getExtraSpecs());
    }
}
