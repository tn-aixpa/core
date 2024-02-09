package it.smartcommunitylabdhub.framework.k8s.kubernetes.kaniko;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JobBuildConfig {

    private String type;
    private String uuid;
    private String name;

    public String getIdentifier() {
        return "-" + type + "-" + uuid;
    }
}
