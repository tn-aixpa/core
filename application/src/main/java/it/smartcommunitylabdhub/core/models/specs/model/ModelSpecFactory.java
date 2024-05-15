package it.smartcommunitylabdhub.core.models.specs.model;

import it.smartcommunitylabdhub.commons.infrastructure.SpecFactory;
import java.io.Serializable;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class ModelSpecFactory implements SpecFactory<ModelSpec> {

    @Override
    public ModelSpec create() {
        return new ModelSpec();
    }

    @Override
    public ModelSpec create(Map<String, Serializable> data) {
        ModelSpec spec = new ModelSpec();
        spec.configure(data);

        return spec;
    }
}
