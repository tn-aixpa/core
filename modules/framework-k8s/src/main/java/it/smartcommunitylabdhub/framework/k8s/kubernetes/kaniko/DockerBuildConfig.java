package it.smartcommunitylabdhub.framework.k8s.kubernetes.kaniko;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DockerBuildConfig {
    private String dockerTemplatePath;
    private String dockerTargetPath;
    private String baseImage;
    private String entrypointCommand;
    private String sharedData;
    private String dirData;

    @Builder.Default
    private List<String> additionalCommands = new ArrayList<>();

    public DockerBuildConfig addCommand(String value) {
        additionalCommands.add(value);
        return this;
    }
}
