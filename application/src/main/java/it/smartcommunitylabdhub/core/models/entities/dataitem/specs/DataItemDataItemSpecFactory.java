package it.smartcommunitylabdhub.core.models.entities.dataitem.specs;

import it.smartcommunitylabdhub.commons.infrastructure.SpecFactory;
import java.io.Serializable;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class DataItemDataItemSpecFactory implements SpecFactory<DataItemDataItemSpec> {

    @Override
    public DataItemDataItemSpec create() {
        return new DataItemDataItemSpec();
    }

    @Override
    public DataItemDataItemSpec create(Map<String, Serializable> data) {
        DataItemDataItemSpec spec = new DataItemDataItemSpec();
        spec.configure(data);

        return spec;
    }
}
