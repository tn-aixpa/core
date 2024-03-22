package it.smartcommunitylabdhub.core.models.specs.secret;

import it.smartcommunitylabdhub.commons.infrastructure.SpecFactory;
import java.io.Serializable;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class SecretSecretSpecFactory implements SpecFactory<SecretSecretSpec> {

    @Override
    public SecretSecretSpec create() {
        return new SecretSecretSpec();
    }

    @Override
    public SecretSecretSpec create(Map<String, Serializable> data) {
        SecretSecretSpec spec = new SecretSecretSpec();
        spec.configure(data);

        return spec;
    }
}
