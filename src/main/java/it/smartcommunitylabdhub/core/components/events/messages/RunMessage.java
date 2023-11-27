package it.smartcommunitylabdhub.core.components.events.messages;

import it.smartcommunitylabdhub.core.components.events.messages.interfaces.Message;
import it.smartcommunitylabdhub.core.models.entities.run.XRun;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RunMessage implements Message {
    private XRun runDTO;
}
