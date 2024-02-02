package it.smartcommunitylabdhub.core.models.accessors.kinds.interfaces;

public interface SecretFieldAccessor<O extends SecretFieldAccessor<O>> extends CommonFieldAccessor<O> {

    default String getPath() {
        return mapHasField(getSpecs(), "path") ? (String) getSpecs().get("path") : null;
    }

    default String getProvider() {
        return mapHasField(getSpecs(), "provider") ? (String) getSpecs().get("provider") : null;
    }

    default String getSecretName() {
        return mapHasField(getSpecs(), "secretName") ? (String) getSpecs().get("secretName") : null;
    }

}
