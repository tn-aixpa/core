package it.smartcommunitylabdhub.core.dataitems.specs;

import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.models.dataitem.DataItemBaseSpec;
import it.smartcommunitylabdhub.commons.models.entities.EntityName;
import java.io.Serializable;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SpecType(kind = "dataitem", entity = EntityName.DATAITEM)
public class DataItemDataItemSpec extends DataItemBaseSpec {

    @Override
    public void configure(Map<String, Serializable> data) {
        super.configure(data);
    }
}
