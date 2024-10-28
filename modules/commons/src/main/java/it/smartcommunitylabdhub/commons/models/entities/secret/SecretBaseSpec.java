package it.smartcommunitylabdhub.commons.models.entities.secret;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import it.smartcommunitylabdhub.commons.models.base.BaseSpec;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SecretBaseSpec extends BaseSpec {

    @JsonProperty("path")
    @NotNull
    @Pattern(regexp = "secret://([\\w-]+)")
    String path;

    @NotNull
    @Schema(allowableValues = { "kubernetes" }, defaultValue = "kubernetes")
    String provider;

    @Override
    public void configure(Map<String, Serializable> data) {
        SecretBaseSpec spec = mapper.convertValue(data, SecretBaseSpec.class);

        this.path = spec.getPath();
        this.provider = spec.getProvider();
    }
}
