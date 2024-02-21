package it.smartcommunitylabdhub.core.config;

import it.smartcommunitylabdhub.commons.infrastructure.*;
import it.smartcommunitylabdhub.commons.infrastructure.Runnable;
import it.smartcommunitylabdhub.commons.infrastructure.Runtime;
import it.smartcommunitylabdhub.commons.models.entities.function.FunctionBaseSpec;
import it.smartcommunitylabdhub.commons.models.entities.run.RunBaseSpec;
import it.smartcommunitylabdhub.commons.models.entities.task.TaskBaseSpec;
import it.smartcommunitylabdhub.commons.services.RunnableStoreService;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.builders.BuilderFactory;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.frameworks.FrameworkFactory;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.runners.RunnerFactory;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.runtimes.RuntimeFactory;
import it.smartcommunitylabdhub.core.repositories.RunnableRepository;
import it.smartcommunitylabdhub.core.services.RunnableStoreServiceImpl;
import java.util.List;
import java.util.function.Supplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;

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
    @SuppressWarnings("unchecked")
    protected <T extends Runnable> Supplier<RunnableStoreService<T>> runnableStoreServiceSupplier() {
        return () -> {
            ParameterizedTypeReference<RunnableStoreService<T>> typeReference = new ParameterizedTypeReference<>() {};
            Class<? extends T> clazz = (Class<? extends T>) typeReference.getType();
            return (RunnableStoreService<T>) new RunnableStoreServiceImpl<>(clazz, runnableRepository);
        };
    }
}
