package it.smartcommunitylabdhub.core.models.entities.dataitem.specs;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.smartcommunitylabdhub.core.annotations.common.SpecType;
import it.smartcommunitylabdhub.core.components.infrastructure.enums.EntityName;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@SpecType(kind = "dataitem", entity = EntityName.DATAITEM)
public class DataItemDataItemSpec extends DataItemBaseSpec<DataItemDataItemSpec> {
    @JsonProperty("raw_code")
    private String rawCode;
    @JsonProperty("compiled_code")
    private String compiledCode;

    @Override
    protected void configureSpec(DataItemDataItemSpec dataItemDbtSpec) {
        super.configureSpec(dataItemDbtSpec);

        this.setRawCode(dataItemDbtSpec.getRawCode());
        this.setCompiledCode(dataItemDbtSpec.getCompiledCode());
    }
}
