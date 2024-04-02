package it.smartcommunitylabdhub.core.models.entities.runnable;

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

    private Date created;

    private Date updated;

    private String clazz;

    private byte[] data;
}
