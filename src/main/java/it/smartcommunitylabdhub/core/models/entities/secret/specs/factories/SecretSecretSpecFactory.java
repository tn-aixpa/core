package it.smartcommunitylabdhub.core.models.entities.secret.specs.factories;

import it.smartcommunitylabdhub.core.components.infrastructure.factories.specs.SpecFactory;
import it.smartcommunitylabdhub.core.models.entities.secret.specs.SecretSecretSpec;

import org.springframework.stereotype.Component;

@Component
public class SecretSecretSpecFactory implements SpecFactory<SecretSecretSpec> {
    @Override
    public SecretSecretSpec create() {
        return new SecretSecretSpec();
    }
}
