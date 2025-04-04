package it.smartcommunitylabdhub.framework.k8s.kubernetes;

import it.smartcommunitylabdhub.commons.config.ApplicationProperties;
import it.smartcommunitylabdhub.commons.utils.MapUtils;
import it.smartcommunitylabdhub.framework.k8s.annotations.ConditionalOnKubernetes;
import it.smartcommunitylabdhub.framework.k8s.runnables.K8sRunnable;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnKubernetes
public class K8sLabelHelper {

    @Autowired
    ApplicationProperties applicationProperties;

    public <T extends K8sRunnable> Map<String, String> buildBaseLabels(@NotNull T runnable) {
        // Create labels for app
        Map<String, String> appLabels = buildAppLabels(runnable);

        //create custom core labels
        Map<String, String> coreLabels = buildCoreLabels(runnable);

        //merge, no overlap
        return MapUtils.mergeMultipleMaps(appLabels, coreLabels);
    }

    public <T extends K8sRunnable> Map<String, String> buildAppLabels(@NotNull T runnable) {
        // Create standard app labels
        return Map.of(
            "app.kubernetes.io/instance",
            K8sBuilderHelper.sanitizeNames(applicationProperties.getName() + "-" + runnable.getId()),
            "app.kubernetes.io/version",
            runnable.getId(),
            "app.kubernetes.io/part-of",
            K8sBuilderHelper.sanitizeNames(applicationProperties.getName() + "-" + runnable.getProject()),
            "app.kubernetes.io/managed-by",
            K8sBuilderHelper.sanitizeNames(applicationProperties.getName())
        );
    }

    public <T extends K8sRunnable> Map<String, String> buildCoreLabels(@NotNull T runnable) {
        //create custom core labels
        String prefix = getCoreLabelsNamespace() + "/";

        return Map.of(
            prefix + "project",
            K8sBuilderHelper.sanitizeNames(runnable.getProject()),
            prefix + "framework",
            K8sBuilderHelper.sanitizeNames(runnable.getFramework()),
            prefix + "runtime",
            K8sBuilderHelper.sanitizeNames(runnable.getRuntime()),
            prefix + "run",
            K8sBuilderHelper.sanitizeNames(runnable.getId())
        );
    }

    public String getCoreLabelsNamespace() {
        return K8sBuilderHelper.sanitizeNames(applicationProperties.getName());
    }

    public Map<String, String> extractCoreLabels(@NotNull Map<String, String> labels) {
        String prefix = K8sBuilderHelper.sanitizeNames(applicationProperties.getName()) + "/";

        List<String> coreLabels = List.of("project", "framework", "runtime", "run");

        return labels
            .entrySet()
            .stream()
            .filter(entry -> coreLabels.contains(entry.getKey().replace(prefix, "")))
            .collect(Collectors.toMap(entry -> entry.getKey().replace(prefix, ""), Map.Entry::getValue));
    }
}
