package it.smartcommunitylabdhub.core.models.specs.dataitem;

import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.models.dataitem.DataItemBaseSpec;
import it.smartcommunitylabdhub.commons.models.entities.EntityName;
import java.io.Serializable;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SpecType(kind = "table", entity = EntityName.DATAITEM)
public class DataItemTableSpec extends DataItemBaseSpec {

    //TODO adopt tableschema
    //see https://github.com/frictionlessdata/tableschema-java
    // private Map<String, Serializable> schema;
    private TableSchema schema;

    @Override
    public void configure(Map<String, Serializable> data) {
        super.configure(data);

        DataItemTableSpec spec = mapper.convertValue(data, DataItemTableSpec.class);

        this.schema = spec.getSchema();
    }
}
