package it.smartcommunitylabdhub.dataitems.specs;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.smartcommunitylabdhub.commons.models.base.BaseSpec;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DataItemBaseSpec extends BaseSpec {

    @NotBlank
    @JsonProperty("path")
    private String path;

    @Override
    public void configure(Map<String, Serializable> data) {
        DataItemBaseSpec concreteSpec = mapper.convertValue(data, DataItemBaseSpec.class);

        this.path = concreteSpec.getPath();
    }
}
