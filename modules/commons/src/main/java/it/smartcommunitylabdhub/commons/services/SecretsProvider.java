package it.smartcommunitylabdhub.commons.services;

import it.smartcommunitylabdhub.commons.exceptions.StoreException;
import jakarta.validation.constraints.NotNull;

public interface SecretsProvider {
    public String readSecretData(@NotNull String path) throws StoreException;

    public void writeSecretData(@NotNull String path, @NotNull String value) throws StoreException;

    public void clearSecretData(@NotNull String path) throws StoreException;
}
