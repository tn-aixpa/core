package it.smartcommunitylabdhub.modules.mlrun.components.runnables.events.services;

import it.smartcommunitylabdhub.core.components.events.services.interfaces.KindService;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.specs.SpecEntity;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.specs.SpecRegistry;
import it.smartcommunitylabdhub.core.exceptions.CoreException;
import it.smartcommunitylabdhub.core.models.accessors.utils.TaskAccessor;
import it.smartcommunitylabdhub.core.models.accessors.utils.TaskUtils;
import it.smartcommunitylabdhub.core.models.base.interfaces.Spec;
import it.smartcommunitylabdhub.core.models.entities.run.Run;
import it.smartcommunitylabdhub.core.models.entities.run.specs.RunBaseSpec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;

@Service
@Qualifier("JobService")
@Slf4j
public class JobServiceImpl implements KindService<Map<String, Object>> {

    private final RestTemplate restTemplate;
    @Autowired
    SpecRegistry<? extends Spec> specRegistry;

    @Value("${mlrun.api.submit-job}")
    private String MLRUN_API_SUBMIT_JOB;

    public JobServiceImpl() {
        this.restTemplate = new RestTemplate();
    }

    @Override
    public Map<String, Object> run(Run runDTO) {
        ParameterizedTypeReference<Map<String, Object>> responseType =
                new ParameterizedTypeReference<>() {
                };

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        RunBaseSpec<?> runBaseSpec = specRegistry.createSpec(
                runDTO.getKind(),
                SpecEntity.RUN,
                runDTO.getSpec()
        );
        TaskAccessor taskAccessor = TaskUtils.parseTask(runBaseSpec.getTask());
        Map<String, Object> requestBody =
                Map.of("task", Map.of("spec", runDTO.getSpec(),
                        "metadata", Map.of("name",
                                taskAccessor.getProject() + "-" + taskAccessor.getName(),
                                "project", runDTO.getProject())));

        log.info("-----------------  REQUEST BODY ----------------");
        log.info(requestBody.toString());
        log.info("-----------------  end REQUEST BODY ----------------");
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map<String, Object>> response =
                restTemplate.exchange(MLRUN_API_SUBMIT_JOB, HttpMethod.POST, entity, responseType);

        if (response.getStatusCode().is2xxSuccessful()) {
            return Optional.ofNullable(response.getBody()).orElse(null);
        } else {
            String statusCode = response.getStatusCode().toString();
            String errorMessage = Optional.ofNullable(response.getBody())
                    .map(body -> body.get("detail"))
                    .map(Object::toString)
                    .orElse("");

            throw new CoreException(statusCode, errorMessage, null);
        }
    }
}
