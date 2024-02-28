package it.smartcommunitylabdhub.commons.events;

import java.util.List;
import lombok.*;


@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RunChangedEvent {
    private List<RunMonitorObject> monitorObjects;
}
