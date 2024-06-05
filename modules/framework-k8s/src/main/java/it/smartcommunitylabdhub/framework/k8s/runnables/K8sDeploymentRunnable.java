package it.smartcommunitylabdhub.framework.k8s.runnables;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.smartcommunitylabdhub.commons.annotations.infrastructure.RunnableComponent;
import it.smartcommunitylabdhub.framework.k8s.infrastructure.k8s.K8sDeploymentFramework;
import it.smartcommunitylabdhub.framework.k8s.model.ContextRef;
import it.smartcommunitylabdhub.framework.k8s.model.ContextSource;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@RunnableComponent(framework = K8sDeploymentFramework.FRAMEWORK)
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class K8sDeploymentRunnable extends K8sRunnable {

    @JsonProperty("context_refs")
    private List<ContextRef> contextRefs;

    @JsonProperty("context_sources")
    private List<ContextSource> contextSources;

    private Integer replicas;

    @Override
    public String getFramework() {
        return K8sDeploymentFramework.FRAMEWORK;
    }
}
