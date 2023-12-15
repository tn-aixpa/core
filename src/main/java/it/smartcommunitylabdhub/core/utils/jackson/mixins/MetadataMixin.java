package it.smartcommunitylabdhub.core.utils.jackson.mixins;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import it.smartcommunitylabdhub.core.utils.jackson.serializers.CborByteArrayDeserializer;
import it.smartcommunitylabdhub.core.utils.jackson.serializers.CborByteArraySerializer;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class MetadataMixin {

    @JsonSerialize(using = CborByteArraySerializer.class)
    @JsonDeserialize(using = CborByteArrayDeserializer.class)
    public Map<String, Object> metadata;
}
