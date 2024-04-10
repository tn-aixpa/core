package it.smartcommunitylabdhub.commons.models.entities.log;

import com.fasterxml.jackson.annotation.JsonInclude;
import it.smartcommunitylabdhub.commons.models.base.BaseSpec;
import it.smartcommunitylabdhub.commons.models.metadata.Metadata;
import java.io.Serializable;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LogMetadata extends BaseSpec implements Metadata {

    private String run;

    @Override
    public void configure(Map<String, Serializable> data) {
        LogMetadata meta = mapper.convertValue(data, LogMetadata.class);

        this.run = meta.getRun();
    }

    public static LogMetadata from(Map<String, Serializable> map) {
        LogMetadata meta = new LogMetadata();
        meta.configure(map);

        return meta;
    }
}
