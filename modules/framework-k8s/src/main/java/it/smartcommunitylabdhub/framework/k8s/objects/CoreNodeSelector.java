package it.smartcommunitylabdhub.framework.k8s.objects;

import java.io.Serializable;

public record CoreNodeSelector(String key, String value) implements Serializable {}
