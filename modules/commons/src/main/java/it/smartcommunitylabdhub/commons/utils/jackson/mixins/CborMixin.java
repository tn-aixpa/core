package it.smartcommunitylabdhub.commons.utils.jackson.mixins;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import it.smartcommunitylabdhub.commons.utils.jackson.serializers.CborSerializer;

public abstract class CborMixin {
    
    @JsonSerialize(using = CborSerializer.class)
    private byte[] metadata;

    @JsonSerialize(using = CborSerializer.class)
    private byte[] spec;

    @JsonSerialize(using = CborSerializer.class)
    private byte[] extra;

    @JsonSerialize(using = CborSerializer.class)
    private byte[] status;
}
