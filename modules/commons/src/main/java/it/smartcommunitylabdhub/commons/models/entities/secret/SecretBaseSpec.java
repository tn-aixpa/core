package it.smartcommunitylabdhub.commons.models.entities.secret;

import it.smartcommunitylabdhub.commons.models.base.BaseSpec;
import java.io.Serializable;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SecretBaseSpec extends BaseSpec {

    //TODO remove, these are for `secret` kind
    String path;
    String provider;

    @Override
    public void configure(Map<String, Serializable> data) {
        SecretBaseSpec concreteSpec = mapper.convertValue(data, SecretBaseSpec.class);

        this.path = concreteSpec.getPath();
        this.provider = concreteSpec.getProvider();
    }
}
