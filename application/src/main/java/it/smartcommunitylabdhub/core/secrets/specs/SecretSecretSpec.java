package it.smartcommunitylabdhub.core.secrets.specs;

import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.models.entities.EntityName;
import it.smartcommunitylabdhub.commons.models.secret.SecretBaseSpec;
import java.io.Serializable;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@SpecType(kind = "secret", entity = EntityName.SECRET)
public class SecretSecretSpec extends SecretBaseSpec {

    @Override
    public void configure(Map<String, Serializable> data) {
        super.configure(data);
    }
}
