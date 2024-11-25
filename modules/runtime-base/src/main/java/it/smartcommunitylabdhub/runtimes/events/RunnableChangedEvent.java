package it.smartcommunitylabdhub.runtimes.events;

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

    private R runnable;
    private String state;
    private String previousState;

    public String getId() {
        return runnable != null ? runnable.getId() : null;
    }

    public String getState() {
        return state != null ? state : runnable != null ? runnable.getState() : null;
    }

    public static <R extends RunRunnable> RunnableChangedEvent<R> build(R runnable, String previousState) {
        return new RunnableChangedEvent<>(runnable, null, previousState);
    }
}
