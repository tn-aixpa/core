package it.smartcommunitylabdhub.commons.models.accessors.fields;

import java.util.Map;

public interface SecretFieldAccessor extends CommonFieldAccessor {
    default String getPath() {
        return mapHasField(getSpecs(), "path") ? (String) getSpecs().get("path") : null;
    }

    default String getProvider() {
        return mapHasField(getSpecs(), "provider") ? (String) getSpecs().get("provider") : null;
    }

    default String getSecretName() {
        return mapHasField(getSpecs(), "secretName") ? (String) getSpecs().get("secretName") : null;
    }

    static SecretFieldAccessor with(Map<String, Object> map) {
        return () -> map;
    }
}
