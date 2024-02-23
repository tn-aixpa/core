package it.smartcommunitylabdhub.core.config;

import it.smartcommunitylabdhub.commons.infrastructure.*;
import it.smartcommunitylabdhub.commons.infrastructure.Runnable;
import it.smartcommunitylabdhub.commons.infrastructure.Runtime;
import it.smartcommunitylabdhub.commons.models.entities.function.FunctionBaseSpec;
import it.smartcommunitylabdhub.commons.models.entities.run.RunBaseSpec;
import it.smartcommunitylabdhub.commons.models.entities.task.TaskBaseSpec;
import it.smartcommunitylabdhub.commons.services.RunnableStore;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.builders.BuilderFactory;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.frameworks.FrameworkFactory;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.runners.RunnerFactory;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.runtimes.RuntimeFactory;
import it.smartcommunitylabdhub.core.repositories.RunnableRepository;
import it.smartcommunitylabdhub.core.services.RunnableStoreServiceImpl;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InfrastructureConfig {

    @Autowired
    private RunnableRepository runnableRepository;

    @Bean
    protected FrameworkFactory frameworkFactory(List<Framework<?>> frameworks) {
        return new FrameworkFactory(frameworks);
    }

    @Bean
    protected RuntimeFactory runtimeFactory(
        List<Runtime<? extends FunctionBaseSpec, ? extends RunBaseSpec, ? extends Runnable>> runtimes
    ) {
        return new RuntimeFactory(runtimes);
    }

    @Bean
    protected BuilderFactory builderFactory(
        List<Builder<? extends FunctionBaseSpec, ? extends TaskBaseSpec, ? extends RunBaseSpec>> builders
    ) {
        return new BuilderFactory(builders);
    }

    @Bean
    protected RunnerFactory runnerFactory(List<Runner<? extends Runnable>> runners) {
        return new RunnerFactory(runners);
    }

    @Bean
    protected RunnableStore.StoreSupplier runnableStoreService() {
        return new RunnableStore.StoreSupplier() {
            @Override
            public <T extends Runnable> RunnableStore<T> get(Class<T> clazz) {
                return new RunnableStoreServiceImpl<T>(clazz, runnableRepository);
            }
        };
    }
}
