package it.smartcommunitylabdhub.core.models.entities.dataitem.specs;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.models.entities.dataitem.DataItemBaseSpec;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import java.io.Serializable;
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
    public void configure(Map<String, Serializable> data) {
        super.configure(data);

        DataItemDataItemSpec dataItemDataItemSpec = mapper.convertValue(data, DataItemDataItemSpec.class);

        this.setRawCode(dataItemDataItemSpec.getRawCode());
        this.setCompiledCode(dataItemDataItemSpec.getCompiledCode());
    }
}
