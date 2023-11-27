package it.smartcommunitylabdhub.modules.mlrun.components.runnables.events.listeners;

import it.smartcommunitylabdhub.core.components.events.messages.RunMessage;
import it.smartcommunitylabdhub.core.components.events.services.interfaces.KindService;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.specs.SpecEntity;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.specs.SpecRegistry;
import it.smartcommunitylabdhub.core.exceptions.CoreException;
import it.smartcommunitylabdhub.core.models.base.interfaces.Spec;
import it.smartcommunitylabdhub.core.models.entities.run.Run;
import it.smartcommunitylabdhub.core.models.entities.run.specs.RunBaseSpec;
import it.smartcommunitylabdhub.core.services.interfaces.RunService;
import it.smartcommunitylabdhub.core.utils.MapUtils;
import it.smartcommunitylabdhub.modules.mlrun.components.runnables.events.messages.JobMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
public class JobEventListener {

    @Autowired
    @Qualifier("JobService")
    KindService<Map<String, Object>> jobService;

    @Autowired
    ApplicationEventPublisher eventPublisher;

    @Autowired
    SpecRegistry<? extends Spec> specRegistry;

    @Autowired
    RunService runService;

    @EventListener
    @Async
    public void handle(JobMessage message) {

        RunBaseSpec<?> runBaseSpec = specRegistry.createSpec(
                message.getRunDTO().getKind(),
                SpecEntity.RUN,
                message.getRunDTO().getSpec()
        );

        String threadName = Thread.currentThread().getName();
        log.info("Job Service receive [" + threadName + "] task@"
                + runBaseSpec.getTaskId()
                + ":Job@"
                + message.getRunDTO().getId());

        try {

            Map<String, Object> body = jobService.run(message.getRunDTO());

            // 3. Check the result and perform actions accordingly
            Optional.ofNullable(body)
                    .ifPresentOrElse(
                            response -> handleSuccessfulResponse(response, message.getRunDTO()),
                            () -> handleFailedResponse(
                                    "NullBody",
                                    "No run was found on MLRun"));
        } catch (CoreException e) {
            // Handle the CoreException thrown by jobService.run() method
            // You can log the exception or perform any other necessary actions
            // For example:
            handleFailedResponse(e.getErrorCode(), e.getMessage());
        }
    }

    private void handleSuccessfulResponse(Map<String, Object> response, Run runDTO) {
        Optional<Map<String, Object>> optionalData = MapUtils.getNestedFieldValue(response, "data");

        optionalData.ifPresentOrElse(
                data -> {
                    MapUtils.getNestedFieldValue(data, "metadata").ifPresent(metadata -> {
                        runDTO.setExtra("mlrun_run_uid", metadata.get("uid"));
                    });

                    // Save RunDTO
                    Run savedRunDTO = runService.save(runDTO);

                    log.info("Dispatch event to RunMessage");
                    eventPublisher.publishEvent(RunMessage.builder().runDTO(savedRunDTO).build());
                },
                () -> handleFailedResponse("DataNotPresent",
                        "Data is not present in MLRun Run response."));
    }

    private void handleFailedResponse(String statusCode, String errorMessage) {
        throw new CoreException(statusCode, errorMessage, null);
    }
}
