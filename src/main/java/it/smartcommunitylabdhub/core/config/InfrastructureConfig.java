package it.smartcommunitylabdhub.core.config;

import it.smartcommunitylabdhub.core.components.infrastructure.factories.builders.Builder;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.builders.BuilderFactory;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.frameworks.Framework;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.frameworks.FrameworkFactory;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.runners.Runner;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.runners.RunnerFactory;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.runtimes.Runtime;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.runtimes.RuntimeFactory;
import it.smartcommunitylabdhub.core.models.entities.function.specs.FunctionBaseSpec;
import it.smartcommunitylabdhub.core.models.entities.run.specs.RunBaseSpec;
import it.smartcommunitylabdhub.core.models.entities.task.specs.K8sTaskBaseSpec;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class InfrastructureConfig {

    @Bean
    protected FrameworkFactory frameworkFactory(
            List<Framework<?>> frameworks) {
        return new FrameworkFactory(frameworks);
    }

    @Bean
    protected RuntimeFactory runtimeFactory(
            List<Runtime<? extends FunctionBaseSpec>> runtimes) {
        return new RuntimeFactory(runtimes);
    }

    @Bean
    protected BuilderFactory builderFactory(
            List<Builder<
                    ? extends FunctionBaseSpec,
                    ? extends K8sTaskBaseSpec,
                    ? extends RunBaseSpec>> builders) {
        return new BuilderFactory(builders);
    }

    @Bean
    protected RunnerFactory runnerFactory(
            List<Runner> runners) {
        return new RunnerFactory(runners);
    }
}
