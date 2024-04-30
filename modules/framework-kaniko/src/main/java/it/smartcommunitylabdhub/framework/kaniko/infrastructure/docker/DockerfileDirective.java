package it.smartcommunitylabdhub.framework.kaniko.infrastructure.docker;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class DockerfileDirective {

    @NotBlank
    private String directive;

    @NotBlank
    private String value;

    @JsonIgnore
    public String write() {
        return String.format("# %s=%s", directive, value);
    }
}
