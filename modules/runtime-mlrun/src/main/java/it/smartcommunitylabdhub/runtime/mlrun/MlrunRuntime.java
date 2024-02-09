package it.smartcommunitylabdhub.runtime.mlrun;

import it.smartcommunitylabdhub.commons.annotations.infrastructure.RuntimeComponent;
import it.smartcommunitylabdhub.commons.exceptions.CoreException;
import it.smartcommunitylabdhub.commons.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.commons.infrastructure.factories.runtimes.Runtime;
import it.smartcommunitylabdhub.commons.infrastructure.factories.specs.SpecRegistry;
import it.smartcommunitylabdhub.commons.models.base.RunStatus;
import it.smartcommunitylabdhub.commons.models.entities.run.Run;
import it.smartcommunitylabdhub.commons.models.entities.run.specs.RunBaseSpec;
import it.smartcommunitylabdhub.commons.models.entities.task.specs.TaskBaseSpec;
import it.smartcommunitylabdhub.commons.services.interfaces.ProjectSecretService;
import it.smartcommunitylabdhub.commons.utils.ErrorList;
import it.smartcommunitylabdhub.framework.k8s.runnables.K8sJobRunnable;
import it.smartcommunitylabdhub.runtime.mlrun.builders.MlrunMlrunBuilder;
import it.smartcommunitylabdhub.runtime.mlrun.models.specs.function.FunctionMlrunSpec;
import it.smartcommunitylabdhub.runtime.mlrun.models.specs.run.RunMlrunSpec;
import it.smartcommunitylabdhub.runtime.mlrun.models.specs.run.RunMlrunSpecFactory;
import it.smartcommunitylabdhub.runtime.mlrun.models.specs.task.TaskMlrunSpec;
import it.smartcommunitylabdhub.runtime.mlrun.runners.MlrunMlrunRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;

@RuntimeComponent(runtime = MlrunRuntime.RUNTIME)
public class MlrunRuntime implements Runtime<FunctionMlrunSpec, RunMlrunSpec, K8sJobRunnable> {

    public static final String RUNTIME = "mlrun";

    @Autowired
    SpecRegistry specRegistry;

    @Autowired
    RunMlrunSpecFactory runMlrunSpecFactory;

    @Autowired
    ProjectSecretService secretService;

    @Value("${runtime.mlrun.image}")
    private String image;

    @Override
    public RunMlrunSpec build(FunctionMlrunSpec funSpec, TaskBaseSpec taskSpec, RunBaseSpec runSpec, String kind) {
        // Retrieve builder using task kind
        if (kind.equals("mlrun")) {
            TaskMlrunSpec taskMlrunSpec = specRegistry.createSpec("mlrun", "mlrun", EntityName.TASK, taskSpec.toMap());

            RunMlrunSpec runMlrunSpec = specRegistry.createSpec("mlrun", "run", EntityName.RUN, runSpec.toMap());

            /**
             *  As an alternative, you can use the code below to retrieve the correct builder.
             *  Remember that if you follow this path, you still need to retrieve the SpecRegistry
             *  either with the @Autowired annotation or with the BeanProvider. If you want to retrieve
             *  the builder in this way remember also that you have to register the builder as
             *  a component using the follow annotation: `@BuilderComponent(runtime = "mlrun", task = "mlrun")`
             *  Only by doing this you can get the bean related
             * <p>
             *      MlrunMlrunBuilder b = getBuilder("mlrun");
             */

            MlrunMlrunBuilder builder = new MlrunMlrunBuilder();

            return builder.build(funSpec, taskMlrunSpec, runMlrunSpec);
        }

        throw new CoreException(
            ErrorList.INTERNAL_SERVER_ERROR.getValue(),
            "Kind not recognized. Cannot retrieve the right builder or specialize Spec for Run and Task.",
            HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @Override
    public K8sJobRunnable run(Run runDTO) {
        /**
         *  As an alternative, you can use the code below to retrieve the correct runner.
         *  Remember that if you follow this path, you still need to retrieve the SpecRegistry
         *  either with the @Autowired annotation or with the BeanProvider. If you want to retrieve
         *  the Runner using specRegistry remember that you have to register also each Runner
         *  component in this way : `@RunnerComponent(runtime = "mlrun", task = "mlrun")`
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
        RunMlrunSpec runRunSpec = runMlrunSpecFactory.create();
        runRunSpec.configure(runDTO.getSpec());

        MlrunMlrunRunner runner = new MlrunMlrunRunner(
            image,
            secretService.groupSecrets(runDTO.getProject(), runRunSpec.getTaskSpec().getSecrets())
        );

        return runner.produce(runDTO);
    }

    @Override
    public RunStatus parse() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'parse'");
    }
}
