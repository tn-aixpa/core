package it.smartcommunitylabdhub.core.models.entities.secret.specs;

import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.jackson.JacksonMapper;
import it.smartcommunitylabdhub.commons.models.entities.secret.SecretBaseSpec;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SpecType(kind = "secret", entity = EntityName.SECRET)
public class SecretSecretSpec extends SecretBaseSpec {

    @Override
    public void configure(Map<String, Object> data) {
        SecretSecretSpec secretSecretSpec = JacksonMapper.CUSTOM_OBJECT_MAPPER.convertValue(
            data,
            SecretSecretSpec.class
        );

        super.configure(data);
        this.setExtraSpecs(secretSecretSpec.getExtraSpecs());
    }
}
