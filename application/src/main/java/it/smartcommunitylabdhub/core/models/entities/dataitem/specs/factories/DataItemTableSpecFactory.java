package it.smartcommunitylabdhub.core.models.entities.dataitem.specs.factories;

import it.smartcommunitylabdhub.commons.infrastructure.factories.specs.SpecFactory;
import it.smartcommunitylabdhub.core.models.entities.dataitem.specs.DataItemTableSpec;
import org.springframework.stereotype.Component;

@Component
public class DataItemTableSpecFactory implements SpecFactory<DataItemTableSpec> {

    @Override
    public DataItemTableSpec create() {
        return new DataItemTableSpec();
    }
}
