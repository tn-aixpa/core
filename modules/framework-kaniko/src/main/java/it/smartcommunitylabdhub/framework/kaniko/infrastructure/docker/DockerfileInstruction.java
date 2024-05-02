package it.smartcommunitylabdhub.framework.kaniko.infrastructure.docker;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class DockerfileInstruction {

    @NotNull
    private Kind instruction;

    //options as required by the instruction
    //example: --mount=type=cache,target=/root/.cache/go-build
    private String[] opts;

    private String[] args;

    @JsonIgnore
    public String write() {
        StringBuilder sb = new StringBuilder();
        sb.append(instruction.name()).append(" ");
        if (opts != null) {
            sb.append(String.join(" ", opts));
        }
        if (args != null) {
            sb.append(String.join(" ", args));
        }

        return sb.toString();
    }

    public enum Kind {
        RUN,
        CMD,
        LABEL,
        COPY,
        ADD,
        WORKDIR,
        ENV,
        ARG,
        ENTRYPOINT,
        EXPOSE,
        VOLUME,
        USER,
        HEALTHCHECK,
        SHELL,
        FROM,
    }
}
