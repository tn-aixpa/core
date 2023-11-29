package it.smartcommunitylabdhub.core.models.accessors.kinds.interfaces;

import it.smartcommunitylabdhub.core.models.converters.ConversionUtils;

import java.util.Date;

public interface ProjectFieldAccessor<O extends ProjectFieldAccessor<O>> extends CommonFieldAccessor<O> {

    default String getName() {
        return mapHasField(getMetadata(), "name") ? (String) getMetadata().get("name") : null;
    }

    default Date getCreated() {
        return mapHasField(getMetadata(), "created") ? ConversionUtils.convert(
                getMetadata().get("created"), "datetime") : null;
    }
}
