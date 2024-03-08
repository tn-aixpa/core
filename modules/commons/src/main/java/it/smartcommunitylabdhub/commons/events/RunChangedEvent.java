package it.smartcommunitylabdhub.commons.events;

import it.smartcommunitylabdhub.commons.infrastructure.RunRunnable;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RunChangedEvent<R extends RunRunnable> {

    private RunMonitorObject runMonitorObject;
    private R runnable;
}
