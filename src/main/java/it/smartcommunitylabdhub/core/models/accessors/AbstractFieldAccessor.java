package it.smartcommunitylabdhub.core.models.accessors;

import it.smartcommunitylabdhub.core.models.accessors.kinds.interfaces.CommonFieldAccessor;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractFieldAccessor<O extends AbstractFieldAccessor<O>> implements CommonFieldAccessor<O> {
    protected final Map<String, Object> fields = new HashMap<>();

    @Override
    public Map<String, Object> fields() {
        return this.fields;
    }

    @Override
    public void configure(Map<String, Object> fields) {
        this.fields.clear();
        this.fields.putAll(fields);
    }
}
