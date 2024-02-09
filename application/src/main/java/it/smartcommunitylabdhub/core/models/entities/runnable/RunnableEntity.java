package it.smartcommunitylabdhub.core.models.entities.runnable;

import java.util.Date;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RunnableEntity {

    private String id;

    private byte[] data;

    private Date created;

    private Date updated;
}
