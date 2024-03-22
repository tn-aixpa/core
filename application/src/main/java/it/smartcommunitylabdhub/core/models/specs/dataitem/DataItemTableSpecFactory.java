package it.smartcommunitylabdhub.core.models.specs.dataitem;

import it.smartcommunitylabdhub.commons.infrastructure.SpecFactory;
import java.io.Serializable;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class DataItemTableSpecFactory implements SpecFactory<DataItemTableSpec> {

    @Override
    public DataItemTableSpec create() {
        return new DataItemTableSpec();
    }

    @Override
    public DataItemTableSpec create(Map<String, Serializable> data) {
        DataItemTableSpec spec = new DataItemTableSpec();
        spec.configure(data);

        return spec;
    }
}
