package it.smartcommunitylabdhub.framework.k8s.runnables;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.smartcommunitylabdhub.commons.annotations.infrastructure.RunnableComponent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@RunnableComponent(framework = "k8sjob")
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class K8sJobRunnable extends K8sRunnable {

    @JsonProperty("backoff_limit")
    private Integer backoffLimit;

    //TODO support work-queue style/parallel jobs

    @Override
    public String getFramework() {
        return "k8sjob";
    }
}
