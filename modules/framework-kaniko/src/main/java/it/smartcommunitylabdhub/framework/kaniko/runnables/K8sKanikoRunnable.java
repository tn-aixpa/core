package it.smartcommunitylabdhub.framework.kaniko.runnables;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.smartcommunitylabdhub.commons.annotations.infrastructure.RunnableComponent;
import it.smartcommunitylabdhub.framework.k8s.objects.CoreContainer;
import it.smartcommunitylabdhub.framework.k8s.runnables.K8sRunnable;
import it.smartcommunitylabdhub.framework.kaniko.infrastructure.kaniko.K8sKanikoFramework;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@RunnableComponent(framework = K8sKanikoFramework.FRAMEWORK)
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class K8sKanikoRunnable extends K8sRunnable {


    @JsonProperty("init_containers")
    private CoreContainer initContainer;

    @JsonProperty("backoff_limit")
    private Integer backoffLimit;

    @Override
    public String getFramework() {
        return K8sKanikoFramework.FRAMEWORK;
    }
}
