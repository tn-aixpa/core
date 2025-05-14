package it.smartcommunitylabdhub.core.runs.persistence;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RunnableEntity {

    private String id;

    private String user;

    private Date created;

    private Date updated;

    private String clazz;

    private byte[] data;
}
