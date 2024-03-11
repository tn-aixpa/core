package it.smartcommunitylabdhub.commons.events;

import it.smartcommunitylabdhub.commons.infrastructure.RunRunnable;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RunnableChangedEvent<R extends RunRunnable> {

    private RunnableMonitorObject runMonitorObject;
    private R runnable;
}
