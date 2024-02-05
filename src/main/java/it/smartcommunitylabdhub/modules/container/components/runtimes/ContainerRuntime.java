package it.smartcommunitylabdhub.modules.container.components.runtimes;

import it.smartcommunitylabdhub.core.annotations.infrastructure.RuntimeComponent;
import it.smartcommunitylabdhub.core.components.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.builders.BuilderFactory;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.runnables.Runnable;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.runners.RunnerFactory;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.specs.SpecRegistry;
import it.smartcommunitylabdhub.core.components.infrastructure.runtimes.BaseRuntime;
import it.smartcommunitylabdhub.core.exceptions.CoreException;
import it.smartcommunitylabdhub.core.models.accessors.kinds.runs.RunDefaultFieldAccessor;
import it.smartcommunitylabdhub.core.models.accessors.kinds.runs.factories.RunDefaultFieldAccessorFactory;
import it.smartcommunitylabdhub.core.models.accessors.utils.RunAccessor;
import it.smartcommunitylabdhub.core.models.accessors.utils.RunUtils;
import it.smartcommunitylabdhub.core.models.base.RunStatus;
import it.smartcommunitylabdhub.core.models.base.interfaces.Spec;
import it.smartcommunitylabdhub.core.models.entities.run.Run;
import it.smartcommunitylabdhub.core.models.entities.run.specs.RunBaseSpec;
import it.smartcommunitylabdhub.core.models.entities.run.specs.RunRunSpec;
import it.smartcommunitylabdhub.core.models.entities.run.specs.factories.RunRunSpecFactory;
import it.smartcommunitylabdhub.core.models.entities.task.specs.TaskBaseSpec;
import it.smartcommunitylabdhub.core.utils.ErrorList;
import it.smartcommunitylabdhub.core.utils.jackson.JacksonMapper;
import it.smartcommunitylabdhub.modules.container.components.builders.ContainerDeployBuilder;
import it.smartcommunitylabdhub.modules.container.components.builders.ContainerJobBuilder;
import it.smartcommunitylabdhub.modules.container.components.builders.ContainerServeBuilder;
import it.smartcommunitylabdhub.modules.container.components.runners.ContainerDeployRunner;
import it.smartcommunitylabdhub.modules.container.components.runners.ContainerJobRunner;
import it.smartcommunitylabdhub.modules.container.components.runners.ContainerServeRunner;
import it.smartcommunitylabdhub.modules.container.models.specs.function.FunctionContainerSpec;
import it.smartcommunitylabdhub.modules.container.models.specs.function.factories.FunctionContainerSpecFactory;
import it.smartcommunitylabdhub.modules.container.models.specs.task.TaskDeploySpec;
import it.smartcommunitylabdhub.modules.container.models.specs.task.TaskJobSpec;
import it.smartcommunitylabdhub.modules.container.models.specs.task.TaskServeSpec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

@RuntimeComponent(runtime = "container")
public class ContainerRuntime extends BaseRuntime<FunctionContainerSpec> {

    @Autowired
    SpecRegistry<? extends Spec> specRegistry;


    @Autowired
    FunctionContainerSpecFactory functionContainerSpecFactory;

    @Autowired
    RunDefaultFieldAccessorFactory runDefaultFieldAccessorFactory;

    @Autowired
    RunRunSpecFactory runRunSpecFactory;

    private String image;

    public ContainerRuntime(BuilderFactory builderFactory,
                            RunnerFactory runnerFactory) {
        super(builderFactory, runnerFactory);
    }

    @Override
    public RunBaseSpec build(
            FunctionContainerSpec funSpec,
            TaskBaseSpec taskSpec,
            RunBaseSpec runSpec,
            String kind) {

        // The image here will be used to deploy container
        this.image = funSpec.getImage();

        // Retrieve builder using task kind
        switch (kind) {
            case "deploy" -> {

                TaskDeploySpec taskDeploySpec = specRegistry.createSpec(
                        "deploy",
                        EntityName.TASK,
                        taskSpec.toMap()
                );

                RunRunSpec runRunSpec = specRegistry.createSpec(
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
                        "job",
                        EntityName.TASK,
                        taskSpec.toMap()
                );

                RunRunSpec runRunSpec = specRegistry.createSpec(
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
                        "serve",
                        EntityName.TASK,
                        taskSpec.toMap()
                );

                RunRunSpec runRunSpec = specRegistry.createSpec(
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
        // Crete spec for run
        RunRunSpec runRunSpec = runRunSpecFactory.create();
        runRunSpec.configure(runDTO.getSpec());

        // Create string run accessor from task
        RunAccessor runAccessor = RunUtils.parseRun(runRunSpec.getTask());

        // Create and configure function dbt spec
        FunctionContainerSpec functionContainerSpec = functionContainerSpecFactory.create();
        functionContainerSpec.configure(runDTO.getSpec());

        if (functionContainerSpec.getExtraSpecs() == null) {
            throw new IllegalArgumentException(
                    "Invalid argument: args not found in runDTO spec");
        }

        // Create and configure default run field accessor
        RunDefaultFieldAccessor runDefaultFieldAccessor = runDefaultFieldAccessorFactory.create();
        runDefaultFieldAccessor.configure(
                JacksonMapper.CUSTOM_OBJECT_MAPPER.convertValue(
                        runDTO,
                        JacksonMapper.typeRef)
        );

        return switch (runAccessor.getTask()) {
            case "deploy" -> new ContainerDeployRunner(
                    image,
                    functionContainerSpec,
                    runDefaultFieldAccessor,
                    runAccessor).produce(runDTO);
            case "job" -> new ContainerJobRunner(
                    image,
                    functionContainerSpec,
                    runDefaultFieldAccessor,
                    runAccessor).produce(runDTO);
            case "serve" -> new ContainerServeRunner(
                    image,
                    functionContainerSpec,
                    runDefaultFieldAccessor,
                    runAccessor).produce(runDTO);
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
