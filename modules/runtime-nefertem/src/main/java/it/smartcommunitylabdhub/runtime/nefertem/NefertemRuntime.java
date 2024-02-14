package it.smartcommunitylabdhub.runtime.nefertem;

import it.smartcommunitylabdhub.commons.accessors.spec.RunSpecAccessor;
import it.smartcommunitylabdhub.commons.annotations.infrastructure.RuntimeComponent;
import it.smartcommunitylabdhub.commons.exceptions.CoreException;
import it.smartcommunitylabdhub.commons.infrastructure.Runtime;
import it.smartcommunitylabdhub.commons.models.base.RunStatus;
import it.smartcommunitylabdhub.commons.models.entities.run.Run;
import it.smartcommunitylabdhub.commons.models.entities.run.RunBaseSpec;
import it.smartcommunitylabdhub.commons.models.entities.task.TaskBaseSpec;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.commons.models.utils.RunUtils;
import it.smartcommunitylabdhub.commons.services.ProjectSecretService;
import it.smartcommunitylabdhub.commons.services.SpecRegistry;
import it.smartcommunitylabdhub.commons.utils.ErrorList;
import it.smartcommunitylabdhub.framework.k8s.runnables.K8sJobRunnable;
import it.smartcommunitylabdhub.runtime.nefertem.builders.NefertemInferBuilder;
import it.smartcommunitylabdhub.runtime.nefertem.builders.NefertemMetricBuilder;
import it.smartcommunitylabdhub.runtime.nefertem.builders.NefertemProfileBuilder;
import it.smartcommunitylabdhub.runtime.nefertem.builders.NefertemValidateBuilder;
import it.smartcommunitylabdhub.runtime.nefertem.runners.NefertemInferRunner;
import it.smartcommunitylabdhub.runtime.nefertem.runners.NefertemMetricRunner;
import it.smartcommunitylabdhub.runtime.nefertem.runners.NefertemProfileRunner;
import it.smartcommunitylabdhub.runtime.nefertem.runners.NefertemValidateRunner;
import it.smartcommunitylabdhub.runtime.nefertem.specs.function.FunctionNefertemSpec;
import it.smartcommunitylabdhub.runtime.nefertem.specs.run.RunNefertemSpec;
import it.smartcommunitylabdhub.runtime.nefertem.specs.run.RunNefertemSpecFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;

@RuntimeComponent(runtime = NefertemRuntime.RUNTIME)
public class NefertemRuntime implements Runtime<FunctionNefertemSpec, RunNefertemSpec, K8sJobRunnable> {

    public static final String RUNTIME = "nefertem";

    @Autowired
    SpecRegistry specRegistry;

    @Autowired
    RunNefertemSpecFactory runNefertemSpecFactory;

    @Autowired
    ProjectSecretService secretService;

    @Value("${runtime.nefertem.image}")
    private String image;

    @Override
    public RunNefertemSpec build(
        FunctionNefertemSpec funSpec,
        TaskBaseSpec taskSpec,
        RunBaseSpec runSpec,
        String kind
    ) {
        /**
         *  As an alternative, you can use the code below to retrieve the correct builder.
         *  Remember that if you follow this path, you still need to retrieve the SpecRegistry
         *  either with the @Autowired annotation or with the BeanProvider. If you want to retrieve
         *  the builder in this way remember also that you have to register the builder as
         *  a component using the follow annotation: `@BuilderComponent(runtime = "dbt", task = "transform")`
         *  Only by doing this you can get the bean related
         * <p>
         *      NefertemInferBuilder builder = (NefertemInferBuilder) getBuilder(kind);
         */

        // NefertemInferBuilder b = getBuilder(kind);

        // Retrieve builder using task kind
        switch (kind) {
            case "nefertem+infer" -> {
                NefertemInferBuilder builder = new NefertemInferBuilder();

                return builder.build(
                    funSpec,
                    specRegistry.createSpec("nefertem+infer", EntityName.TASK, taskSpec.toMap()),
                    specRegistry.createSpec("nefertem+run", EntityName.RUN, runSpec.toMap())
                );
            }
            case "nefertem+validate" -> {
                NefertemValidateBuilder builder = new NefertemValidateBuilder();

                return builder.build(
                    funSpec,
                    specRegistry.createSpec("nefertem+validate", EntityName.TASK, taskSpec.toMap()),
                    specRegistry.createSpec("nefertem+run", EntityName.RUN, runSpec.toMap())
                );
            }
            case "nefertem+metric" -> {
                NefertemMetricBuilder builder = new NefertemMetricBuilder();

                return builder.build(
                    funSpec,
                    specRegistry.createSpec("nefertem+metric", EntityName.TASK, taskSpec.toMap()),
                    specRegistry.createSpec("nefertem+run", EntityName.RUN, runSpec.toMap())
                );
            }
            case "nefertem+profile" -> {
                NefertemProfileBuilder builder = new NefertemProfileBuilder();

                return builder.build(
                    funSpec,
                    specRegistry.createSpec("nefertem+profile", EntityName.TASK, taskSpec.toMap()),
                    specRegistry.createSpec("nefertem+run", EntityName.RUN, runSpec.toMap())
                );
            }
            default -> throw new CoreException(
                ErrorList.INTERNAL_SERVER_ERROR.getValue(),
                "Kind not recognized. Cannot retrieve the right builder or specialize Spec for Run and Task.",
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
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
         * <p>
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
        RunNefertemSpec runRunSpec = runNefertemSpecFactory.create();
        runRunSpec.configure(runDTO.getSpec());

        // Create string run accessor from task
        RunSpecAccessor runAccessor = RunUtils.parseRun(runRunSpec.getTask());

        return switch (runAccessor.getTask()) {
            case "infer" -> new NefertemInferRunner(
                image,
                secretService.groupSecrets(runDTO.getProject(), runRunSpec.getTaskInferSpec().getSecrets())
            )
                .produce(runDTO);
            case "validate" -> new NefertemValidateRunner(
                image,
                secretService.groupSecrets(runDTO.getProject(), runRunSpec.getTaskValidateSpec().getSecrets())
            )
                .produce(runDTO);
            case "profile" -> new NefertemProfileRunner(
                image,
                secretService.groupSecrets(runDTO.getProject(), runRunSpec.getTaskProfileSpec().getSecrets())
            )
                .produce(runDTO);
            case "metric" -> new NefertemMetricRunner(
                image,
                secretService.groupSecrets(runDTO.getProject(), runRunSpec.getTaskMetricSpec().getSecrets())
            )
                .produce(runDTO);
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
