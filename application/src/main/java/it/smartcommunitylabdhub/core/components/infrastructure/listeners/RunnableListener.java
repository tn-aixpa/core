package it.smartcommunitylabdhub.core.components.infrastructure.listeners;

import it.smartcommunitylabdhub.commons.infrastructure.factories.frameworks.Framework;
import it.smartcommunitylabdhub.commons.infrastructure.factories.runnables.Runnable;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.frameworks.FrameworkFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class RunnableListener {

    @Autowired
    FrameworkFactory frameworkFactory;

    @Async
    @EventListener
    public <R extends Runnable> void listen(R runnable) {
        Framework<Runnable> framework = frameworkFactory.getFramework(runnable.getFramework());
        framework.execute(runnable);
    }
}
