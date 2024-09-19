package it.smartcommunitylabdhub.commons.events;

import it.smartcommunitylabdhub.commons.infrastructure.RunRunnable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RunnableChangedEvent<R extends RunRunnable> {

    private RunnableMonitorObject runMonitorObject;
    private R runnable;

    public String getId() {
        return runnable != null ? runnable.getId() : null;
    }
}
