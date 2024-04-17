package it.smartcommunitylabdhub.framework.k8s.objects;

import java.io.Serializable;

public record CoreVolumeKeyToPath(String key, String path) implements Serializable {
}
