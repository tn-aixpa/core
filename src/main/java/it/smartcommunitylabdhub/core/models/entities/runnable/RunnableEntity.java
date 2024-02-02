package it.smartcommunitylabdhub.core.models.entities.runnable;

import lombok.*;

import java.util.Date;

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
