package it.smartcommunitylabdhub.modules.container.components.runtimes;

import it.smartcommunitylabdhub.core.annotations.infrastructure.RuntimeComponent;
import it.smartcommunitylabdhub.core.components.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.builders.BuilderFactory;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.runnables.Runnable;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.runners.RunnerFactory;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.specs.SpecRegistry;
import it.smartcommunitylabdhub.core.components.infrastructure.runtimes.BaseRuntime;
import it.smartcommunitylabdhub.core.exceptions.CoreException;
import it.smartcommunitylabdhub.core.models.accessors.utils.RunAccessor;
import it.smartcommunitylabdhub.core.models.accessors.utils.RunUtils;
import it.smartcommunitylabdhub.core.models.base.RunStatus;
import it.smartcommunitylabdhub.core.models.base.interfaces.Spec;
import it.smartcommunitylabdhub.core.models.entities.run.Run;
import it.smartcommunitylabdhub.core.models.entities.run.specs.RunBaseSpec;
import it.smartcommunitylabdhub.core.models.entities.run.specs.RunRunSpec;
import it.smartcommunitylabdhub.core.models.entities.task.specs.K8sTaskBaseSpec;
import it.smartcommunitylabdhub.core.utils.ErrorList;
import it.smartcommunitylabdhub.modules.container.components.builders.ContainerDeployBuilder;
import it.smartcommunitylabdhub.modules.container.components.builders.ContainerJobBuilder;
import it.smartcommunitylabdhub.modules.container.components.runners.ContainerDeployRunner;
import it.smartcommunitylabdhub.modules.container.components.runners.ContainerJobRunner;
import it.smartcommunitylabdhub.modules.container.models.specs.function.FunctionContainerSpec;
import it.smartcommunitylabdhub.modules.container.models.specs.task.TaskDeploySpec;
import it.smartcommunitylabdhub.modules.container.models.specs.task.TaskJobSpec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

@RuntimeComponent(runtime = "container")
public class ContainerRuntime extends BaseRuntime<FunctionContainerSpec> {

    @Autowired
    SpecRegistry<? extends Spec> specRegistry;

    private String image;

    public ContainerRuntime(BuilderFactory builderFactory,
                            RunnerFactory runnerFactory) {
        super(builderFactory, runnerFactory);
    }

    @Override
    public RunBaseSpec build(
            FunctionContainerSpec funSpec,
            K8sTaskBaseSpec taskSpec,
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
        RunRunSpec runBaseSpec = specRegistry.createSpec(
                runDTO.getKind(),
                EntityName.RUN,
                runDTO.getSpec()
        );
        RunAccessor runAccessor = RunUtils.parseRun(runBaseSpec.getTask());

        return switch (runAccessor.getTask()) {
            case "deploy" -> new ContainerDeployRunner(image).produce(runDTO);
            case "job" -> new ContainerJobRunner(image).produce(runDTO);
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
