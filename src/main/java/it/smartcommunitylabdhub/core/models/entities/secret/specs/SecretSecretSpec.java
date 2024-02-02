package it.smartcommunitylabdhub.core.models.entities.secret.specs;

import it.smartcommunitylabdhub.core.annotations.common.SpecType;
import it.smartcommunitylabdhub.core.components.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.core.utils.jackson.JacksonMapper;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@SpecType(kind = "secret", entity = EntityName.SECRET, factory = SecretSecretSpec.class)
public class SecretSecretSpec extends SecretBaseSpec {
    @Override
    public void configure(Map<String, Object> data) {

        SecretSecretSpec secretSecretSpec = JacksonMapper.CUSTOM_OBJECT_MAPPER.convertValue(
                data, SecretSecretSpec.class);

        super.configure(data);
        this.setExtraSpecs(secretSecretSpec.getExtraSpecs());
    }
}

