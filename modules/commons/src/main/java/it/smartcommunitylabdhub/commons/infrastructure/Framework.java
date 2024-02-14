/**
 * Framework.java
 */

package it.smartcommunitylabdhub.commons.infrastructure;

public interface Framework<R extends Runnable> {
    void execute(R runnable);

    void stop(R runnable);

    String status(R runnable);
}
