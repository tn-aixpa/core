package it.smartcommunitylabdhub.runtime.container;

import it.smartcommunitylabdhub.commons.exceptions.CoreException;
import it.smartcommunitylabdhub.commons.infrastructure.factories.specs.SpecRegistry;
import it.smartcommunitylabdhub.commons.services.interfaces.ProjectSecretService;
import it.smartcommunitylabdhub.commons.utils.ErrorList;
import it.smartcommunitylabdhub.commons.utils.jackson.JacksonMapper;
import it.smartcommunitylabdhub.core.annotations.infrastructure.RuntimeComponent;
import it.smartcommunitylabdhub.core.components.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.builders.BuilderFactory;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.runnables.Runnable;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.runners.RunnerFactory;
import it.smartcommunitylabdhub.core.components.infrastructure.runtimes.BaseRuntime;
import it.smartcommunitylabdhub.core.models.accessors.kinds.runs.RunDefaultFieldAccessor;
import it.smartcommunitylabdhub.core.models.accessors.kinds.runs.factories.RunDefaultFieldAccessorFactory;
import it.smartcommunitylabdhub.core.models.accessors.utils.RunAccessor;
import it.smartcommunitylabdhub.core.models.accessors.utils.RunUtils;
import it.smartcommunitylabdhub.core.models.base.RunStatus;
import it.smartcommunitylabdhub.core.models.base.interfaces.Spec;
import it.smartcommunitylabdhub.core.models.entities.run.Run;
import it.smartcommunitylabdhub.core.models.entities.run.specs.RunBaseSpec;
import it.smartcommunitylabdhub.core.models.entities.task.specs.TaskBaseSpec;
import it.smartcommunitylabdhub.modules.container.models.specs.function.FunctionContainerSpec;
import it.smartcommunitylabdhub.modules.container.models.specs.function.factories.FunctionContainerSpecFactory;
import it.smartcommunitylabdhub.modules.container.models.specs.run.RunContainerSpec;
import it.smartcommunitylabdhub.modules.container.models.specs.run.factories.RunContainerSpecFactory;
import it.smartcommunitylabdhub.modules.container.models.specs.task.TaskDeploySpec;
import it.smartcommunitylabdhub.modules.container.models.specs.task.TaskJobSpec;
import it.smartcommunitylabdhub.modules.container.models.specs.task.TaskServeSpec;
import it.smartcommunitylabdhub.runtime.container.builders.ContainerDeployBuilder;
import it.smartcommunitylabdhub.runtime.container.builders.ContainerJobBuilder;
import it.smartcommunitylabdhub.runtime.container.builders.ContainerServeBuilder;
import it.smartcommunitylabdhub.runtime.container.runners.ContainerDeployRunner;
import it.smartcommunitylabdhub.runtime.container.runners.ContainerJobRunner;
import it.smartcommunitylabdhub.runtime.container.runners.ContainerServeRunner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

@RuntimeComponent(runtime = ContainerRuntime.RUNTIME)
public class ContainerRuntime extends BaseRuntime<FunctionContainerSpec, RunContainerSpec, Runnable> {

    public static final String RUNTIME = "container";

    @Autowired
    SpecRegistry<? extends Spec> specRegistry;

    @Autowired
    FunctionContainerSpecFactory functionContainerSpecFactory;

    @Autowired
    RunDefaultFieldAccessorFactory runDefaultFieldAccessorFactory;

    @Autowired
    RunContainerSpecFactory runContainerSpecFactory;

    @Autowired
    ProjectSecretService secretService;

    public ContainerRuntime(BuilderFactory builderFactory,
                            RunnerFactory runnerFactory) {
        super(builderFactory, runnerFactory);
    }

    @Override
    public RunContainerSpec build(
            FunctionContainerSpec funSpec,
            TaskBaseSpec taskSpec,
            RunBaseSpec runSpec,
            String kind) {

        // Retrieve builder using task kind
        switch (kind) {
            case "deploy" -> {

                TaskDeploySpec taskDeploySpec = specRegistry.createSpec(
                        "container",
                        "deploy",
                        EntityName.TASK,
                        taskSpec.toMap()
                );

                RunContainerSpec runRunSpec = specRegistry.createSpec(
                        "container",
                        "run",
                        EntityName.RUN,
                        runSpec.toMap()
                );


                /**
                 *  As an alternative, you can use the code below to retrieve the correct builder.
                 *  Remember that if you follow this path, you still need to retrieve the SpecRegistry
                 *  either with the @Autowired annotation or with the BeanProvider. If you want to retrieve
                 *  the builder in this way remember also that you have to register the builder as
                 *  a component using the follow annotation: `@BuilderComponent(runtime = "container", task = "transform")`
                 *  Only by doing this you can get the bean related
                 * <p>
                 *      ContainerJobBuilder b = getBuilder("transform");
                 */

                ContainerDeployBuilder builder = new ContainerDeployBuilder();

                return builder.build(
                        funSpec,
                        taskDeploySpec,
                        runRunSpec);

            }

            case "job" -> {

                TaskJobSpec taskJobSpec = specRegistry.createSpec(
                        "container",
                        "job",
                        EntityName.TASK,
                        taskSpec.toMap()
                );

                RunContainerSpec runRunSpec = specRegistry.createSpec(
                        "container",
                        "run",
                        EntityName.RUN,
                        runSpec.toMap()
                );


                /**
                 *  As an alternative, you can use the code below to retrieve the correct builder.
                 *  Remember that if you follow this path, you still need to retrieve the SpecRegistry
                 *  either with the @Autowired annotation or with the BeanProvider. If you want to retrieve
                 *  the builder in this way remember also that you have to register the builder as
                 *  a component using the follow annotation: `@BuilderComponent(runtime = "container", task = "transform")`
                 *  Only by doing this you can get the bean related
                 * <p>
                 *      ContainerJobBuilder b = getBuilder("transform");
                 */

                ContainerJobBuilder builder = new ContainerJobBuilder();

                return builder.build(
                        funSpec,
                        taskJobSpec,
                        runRunSpec);

            }

            case "serve" -> {

                TaskServeSpec taskServeSpec = specRegistry.createSpec(
                        "container",
                        "serve",
                        EntityName.TASK,
                        taskSpec.toMap()
                );

                RunContainerSpec runRunSpec = specRegistry.createSpec(
                        "container",
                        "run",
                        EntityName.RUN,
                        runSpec.toMap()
                );


                /**
                 *  As an alternative, you can use the code below to retrieve the correct builder.
                 *  Remember that if you follow this path, you still need to retrieve the SpecRegistry
                 *  either with the @Autowired annotation or with the BeanProvider. If you want to retrieve
                 *  the builder in this way remember also that you have to register the builder as
                 *  a component using the follow annotation: `@BuilderComponent(runtime = "container", task = "transform")`
                 *  Only by doing this you can get the bean related
                 * <p>
                 *      ContainerJobBuilder b = getBuilder("transform");
                 */

                ContainerServeBuilder builder = new ContainerServeBuilder();

                return builder.build(
                        funSpec,
                        taskServeSpec,
                        runRunSpec);

            }

            default -> throw new CoreException(
                    ErrorList.INTERNAL_SERVER_ERROR.getValue(),
                    "Kind not recognized. Cannot retrieve the right builder or specialize Spec for Run and Task.",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }


    @Override
    public Runnable run(Run runDTO) {

        /**
         *  As an alternative, you can use the code below to retrieve the correct runner.
         *  Remember that if you follow this path, you still need to retrieve the SpecRegistry
         *  either with the @Autowired annotation or with the BeanProvider. If you want to retrieve
         *  the Runner using specRegistry remember that you have to register also each Runner
         *  component in this way : `@RunnerComponent(runtime = "container", task = "transform")`
         *  Only by doing this you can get the bean related
         *
         *      // Retrieve base run spec to use task
         *      RunRunSpec runBaseSpec = specRegistry.createSpec(
         *              runDTO.getKind(),
         *              SpecEntity.RUN,
         *              runDTO.getSpec()
         *      );
         *      RunAccessor runAccessor = RunUtils.parseRun(runBaseSpec.getTask());
         *      Runner runner = getRunner(runAccessor.getTask());
         */

        RunContainerSpec runContainerSpec = runContainerSpecFactory.create();
        runContainerSpec.configure(runDTO.getSpec());

        // Create string run accessor from task
        RunAccessor runAccessor = RunUtils.parseRun(runContainerSpec.getTask());


        // Create and configure default run field accessor
        RunDefaultFieldAccessor runDefaultFieldAccessor = runDefaultFieldAccessorFactory.create();
        runDefaultFieldAccessor.configure(
                JacksonMapper.CUSTOM_OBJECT_MAPPER.convertValue(
                        runDTO,
                        JacksonMapper.typeRef)
        );


        return switch (runAccessor.getTask()) {
            case "deploy" -> // Crete spec for run
                    new ContainerDeployRunner(
                            runContainerSpec.getFuncSpec(),
                            runDefaultFieldAccessor,
                            secretService.groupSecrets(
                                    runDTO.getProject(),
                                    runContainerSpec.getTaskDeploySpec().getSecrets()
                            )
                    ).produce(runDTO);
            case "job" -> new ContainerJobRunner(
                    runContainerSpec.getFuncSpec(),
                    runDefaultFieldAccessor,
                    secretService.groupSecrets(
                            runDTO.getProject(),
                            runContainerSpec.getTaskJobSpec().getSecrets())
            ).produce(runDTO);
            case "serve" -> new ContainerServeRunner(
                    runContainerSpec.getFuncSpec(),
                    runDefaultFieldAccessor,
                    secretService.groupSecrets(
                            runDTO.getProject(),
                            runContainerSpec.getTaskServeSpec().getSecrets())
            ).produce(runDTO);
            default -> throw new CoreException(
                    ErrorList.INTERNAL_SERVER_ERROR.getValue(),
                    "Kind not recognized. Cannot retrieve the right Runner",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        };
    }

    @Override
    public RunStatus parse() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'parse'");
    }

}
