package it.smartcommunitylabdhub.framework.k8s.config;

import it.smartcommunitylabdhub.framework.k8s.objects.CoreVolume;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Configuration for k8s frameworks
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class KubernetesProperties {

    //TODO add all props
    private String namespace;
    private CoreVolume sharedVolume;
}
