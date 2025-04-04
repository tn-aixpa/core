package it.smartcommunitylabdhub.framework.k8s.infrastructure.watcher;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class K8sLabelHelper {

    private static final Pattern INSTANCE_PATTERN = Pattern.compile("dhcore-([a-f0-9]{32})");

    public static String extractInstanceId(Map<String, String> labels) {
        if (labels == null) {
            return "Unknown";
        }

        String instanceLabel = labels.get("app.kubernetes.io/instance");
        if (instanceLabel == null) {
            return "Unknown";
        }

        Matcher matcher = INSTANCE_PATTERN.matcher(instanceLabel);
        return matcher.find() ? matcher.group(1) : "Unknown";
    }
}
