package it.smartcommunitylabdhub.framework.k8s.base;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.smartcommunitylabdhub.framework.k8s.objects.CoreAffinity;
import it.smartcommunitylabdhub.framework.k8s.objects.CoreEnv;
import it.smartcommunitylabdhub.framework.k8s.objects.CoreLabel;
import it.smartcommunitylabdhub.framework.k8s.objects.CoreNodeSelector;
import it.smartcommunitylabdhub.framework.k8s.objects.CoreResource;
import it.smartcommunitylabdhub.framework.k8s.objects.CoreToleration;
import it.smartcommunitylabdhub.framework.k8s.objects.CoreVolume;
import jakarta.annotation.Nullable;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class K8sTaskSpec {

    @Nullable
    private List<CoreVolume> volumes;

    @Nullable
    @JsonProperty("node_selector")
    private List<CoreNodeSelector> nodeSelector;

    @Nullable
    private List<CoreEnv> envs;

    @Nullable
    private CoreResource resources;

    @Nullable
    private Set<String> secrets;

    @Nullable
    private CoreAffinity affinity;

    @Nullable
    private List<CoreToleration> tolerations;

    @Nullable
    private List<CoreLabel> labels;

    @Nullable
    @JsonProperty("backoff_limit")
    private Integer backoffLimit;

    @Nullable
    private String schedule;

    @Nullable
    private Integer replicas;
}
