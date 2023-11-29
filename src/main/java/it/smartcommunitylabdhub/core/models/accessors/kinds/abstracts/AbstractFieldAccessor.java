package it.smartcommunitylabdhub.core.models.accessors.kinds.abstracts;

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
    public void build(Map<String, Object> fields) {
        this.fields.clear();
        this.fields.putAll(fields);
    }
}
