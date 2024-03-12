package it.smartcommunitylabdhub.commons.models.objects;

import java.io.Serializable;

public record SourceCode(String source, String code, String base64) implements Serializable {}
