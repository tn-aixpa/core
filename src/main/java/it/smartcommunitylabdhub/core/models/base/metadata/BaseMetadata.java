package it.smartcommunitylabdhub.core.models.base.metadata;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BaseMetadata implements Serializable {
    String project;

    String source;

    Set<String> labels;

    private Date created;

    private Date updated;


}
