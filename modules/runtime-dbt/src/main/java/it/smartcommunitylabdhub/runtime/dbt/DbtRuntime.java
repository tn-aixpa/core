package it.smartcommunitylabdhub.runtime.dbt;

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
import it.smartcommunitylabdhub.runtime.dbt.builders.DbtTransformBuilder;
import it.smartcommunitylabdhub.runtime.dbt.models.specs.function.FunctionDbtSpec;
import it.smartcommunitylabdhub.runtime.dbt.models.specs.run.RunDbtSpec;
import it.smartcommunitylabdhub.runtime.dbt.models.specs.run.factories.RunDbtSpecFactory;
import it.smartcommunitylabdhub.runtime.dbt.models.specs.task.TaskTransformSpec;
import it.smartcommunitylabdhub.runtime.dbt.runners.DbtTransformRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;

@RuntimeComponent(runtime = DbtRuntime.RUNTIME)
public class DbtRuntime implements Runtime<FunctionDbtSpec, RunDbtSpec, K8sJobRunnable> {

    public static final String RUNTIME = "dbt";

    @Autowired
    SpecRegistry specRegistry;

    @Autowired
    RunDbtSpecFactory runDbtSpecFactory;

    @Autowired
    ProjectSecretService secretService;

    @Value("${runtime.dbt.image}")
    private String image;

    @Override
    public RunDbtSpec build(FunctionDbtSpec funSpec, TaskBaseSpec taskSpec, RunBaseSpec runSpec, String kind) {
        // Retrieve builder using task kind
        if (kind.equals("transform")) {
            TaskTransformSpec taskTransformSpec = specRegistry.createSpec(
                    "dbt+transform",
                    EntityName.TASK,
                    taskSpec.toMap()
            );

            RunDbtSpec runDbtSpec = specRegistry.createSpec("dbt+run", EntityName.RUN, runSpec.toMap());

            /**
             *  As an alternative, you can use the code below to retrieve the correct builder.
             *  Remember that if you follow this path, you still need to retrieve the SpecRegistry
             *  either with the @Autowired annotation or with the BeanProvider. If you want to retrieve
             *  the builder in this way remember also that you have to register the builder as
             *  a component using the follow annotation: `@BuilderComponent(runtime = "dbt", task = "transform")`
             *  Only by doing this you can get the bean related
             * <p>
             *      DbtTransformBuilder b = getBuilder("transform");
             */

            DbtTransformBuilder builder = new DbtTransformBuilder();

            return builder.build(funSpec, taskTransformSpec, runDbtSpec);
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
         *  component in this way : `@RunnerComponent(runtime = "dbt", task = "transform")`
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
        RunDbtSpec runDbtSpec = runDbtSpecFactory.create();
        runDbtSpec.configure(runDTO.getSpec());

        DbtTransformRunner runner = new DbtTransformRunner(
                image,
                secretService.groupSecrets(runDTO.getProject(), runDbtSpec.getTaskSpec().getSecrets())
        );

        return runner.produce(runDTO);
    }

    @Override
    public RunStatus parse() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'parse'");
    }
}
