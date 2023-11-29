package it.smartcommunitylabdhub.modules.mlrun.components.pollers.functions;

import it.smartcommunitylabdhub.core.components.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.accessors.AccessorRegistry;
import it.smartcommunitylabdhub.core.components.workflows.factory.Workflow;
import it.smartcommunitylabdhub.core.components.workflows.factory.WorkflowFactory;
import it.smartcommunitylabdhub.core.components.workflows.functions.BaseWorkflowBuilder;
import it.smartcommunitylabdhub.core.models.accessors.kinds.interfaces.Accessor;
import it.smartcommunitylabdhub.core.models.accessors.kinds.interfaces.FunctionFieldAccessor;
import it.smartcommunitylabdhub.core.models.converters.ConversionUtils;
import it.smartcommunitylabdhub.core.models.entities.function.Function;
import it.smartcommunitylabdhub.core.services.interfaces.FunctionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
public class FunctionWorkflowBuilder extends BaseWorkflowBuilder {

    @Autowired
    AccessorRegistry<? extends Accessor<Object>> accessorRegistry;

    @Value("${mlrun.api.function-url}")
    private String functionUrl;
    @Value("${mlrun.api.project-url}")
    private String projectUrl;
    private FunctionService functionService;


    public FunctionWorkflowBuilder(FunctionService functionService) {
        this.functionService = functionService;
    }

    public Workflow build() {

        // COMMENT: call /{project}/{function} api and iterate over them..try to check
        @SuppressWarnings("unchecked")
        java.util.function.Function<String, List<Function>> compareMlrunCoreFunctions = url -> {

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            return functionService.getAllLatestFunctions().stream()
                    .filter(function -> function.getKind().equals("job"))
                    .map(function -> {
                        String requestUrl = url
                                .replace("{project}", function
                                        .getProject())
                                .replace("{function}",
                                        function.getName());

                        try {
                            ResponseEntity<Map<String, Object>> response =
                                    restTemplate.exchange(
                                            requestUrl,
                                            HttpMethod.GET,
                                            entity,
                                            responseType);

                            return Optional.ofNullable(
                                            response.getBody())
                                    .map(body -> {
                                        FunctionFieldAccessor<?> mlrunFunctionAccessor =

                                                accessorRegistry.createAccessor(
                                                        function.getKind(),
                                                        EntityName.FUNCTION,
                                                        (Map<String, Object>) body
                                                                .get("func")
                                                );

                                        if (!mlrunFunctionAccessor
                                                .getHash()
                                                .equals(Optional.ofNullable(
                                                                function
                                                                        .getExtra()
                                                                        .get("mlrun_hash"))
                                                        .orElse(""))) {
                                            // Function
                                            // need to
                                            // be
                                            // updated
                                            // in mlrun
                                            return function;
                                        }
                                        return null;
                                    }).orElseGet(() -> null);

                        } catch (HttpClientErrorException e) {
                            log.error(e.getMessage());
                            HttpStatusCode statusCode =
                                    e.getStatusCode();
                            if (statusCode.is4xxClientError()) {
                                // Function will be created on mlrun
                                return function;
                            }
                            // eventually ignored
                            return null;
                        }
                    }).filter(Objects::nonNull).collect(Collectors.toList());

        };

        // COMMENT: For each function on list update or create a new function in mlrun.
        @SuppressWarnings("unchecked")
        java.util.function.Function<List<Function>, List<Function>> storeFunctions = functions -> {

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            return functions.stream().map(function -> {
                try {
                    String requestUrl = functionUrl
                            .replace("{project}", function.getProject())
                            .replace("{function}", function.getName());

                    // Convert function DTO into Map<String, Object>
                    Map<String, Object> requestBody =
                            ConversionUtils.convert(function,
                                    "mlrunFunction");

                    // Compose request
                    HttpEntity<Map<String, Object>> entity =
                            new HttpEntity<>(requestBody, headers);

                    // Get response
                    ResponseEntity<Map<String, Object>> response =
                            restTemplate.exchange(requestUrl,
                                    HttpMethod.POST, entity,
                                    responseType);

                    if (response.getStatusCode().is2xxSuccessful()) {

                        // Set mlrun -> core : hash
                        Optional.ofNullable(response.getBody())
                                .ifPresent(b -> function.setExtra(
                                        "mlrun_hash",
                                        Optional.ofNullable(
                                                        b.get("hash_key"))
                                                .orElse("")));

                        // Set mlrun -> core : status
                        ResponseEntity<Map<String, Object>> funcResponse =
                                restTemplate
                                        .exchange(requestUrl,
                                                HttpMethod.GET,
                                                entity,
                                                responseType);

                        Optional.ofNullable(funcResponse.getBody())
                                .ifPresent(body -> {
                                    FunctionFieldAccessor mlrunFunctionAccessor =

                                            accessorRegistry.createAccessor(
                                                    function.getKind(),
                                                    EntityName.FUNCTION,
                                                    (Map<String, Object>) body
                                                            .get("func")
                                            );

                                    function.setExtra("status",
                                            mlrunFunctionAccessor
                                                    .getStatus());
                                });

                        // Update our function
                        return functionService.updateFunction(function,
                                function.getId());
                    }
                    return null;
                } catch (HttpClientErrorException ex) {
                    log.error(ex.getMessage());
                    return null;
                }
            }).filter(Objects::nonNull).collect(Collectors.toList());
        };

        /*
         * // COMMENT: Update project with function in mlrun
         *
         * @SuppressWarnings("unchecked") Function<List<FunctionDTO>, Object> updateProject
         * = functions -> { HttpHeaders headers = new HttpHeaders();
         * headers.setContentType(MediaType.APPLICATION_JSON);
         *
         * functions.stream().forEach(function -> { try { String requestUrl = projectUrl
         * .replace("{project}", function.getProject());
         *
         * // Get the project HttpEntity<String> entityGet = new HttpEntity<>(headers);
         * ResponseEntity<Map<String, Object>> response = restTemplate .exchange(requestUrl,
         * HttpMethod.GET, entityGet, responseType);
         *
         * Optional.ofNullable(response.getBody()).ifPresent(body -> { ProjectFieldAccessor
         * projectFieldAccessor = ProjectKind.MLRUN .createAccessor(body);
         *
         * FunctionKind functionKind = FunctionKind.valueOf(
         * function.getKind().toUpperCase()); FunctionFieldAccessor functionFieldAccessor =
         * functionKind .createAccessor(ConversionUtils.convert(function, "mlrunFunction"));
         *
         * // Create a new function into project Map<String, Object> newFunction =
         * Stream.of( new AbstractMap.SimpleEntry<>("url", functionKind.invokeMethod(
         * functionFieldAccessor, "getCodeOrigin")), new AbstractMap.SimpleEntry<>("name",
         * functionFieldAccessor.getName()), new AbstractMap.SimpleEntry<>("kind",
         * functionFieldAccessor.getKind()), new AbstractMap.SimpleEntry<>("image",
         * functionFieldAccessor.getImage()), new AbstractMap.SimpleEntry<>("handler",
         * functionFieldAccessor .getDefaultHandler())) .filter(entry -> entry.getValue() !=
         * null) // exclude // null // values .collect(Collectors.toMap(Map.Entry::getKey,
         * Map.Entry::getValue));
         *
         * ((List<Map<String, Object>>) projectFieldAccessor
         * .getSpecs().get("functions")).add(newFunction);
         *
         * HttpEntity<Map<String, Object>> entityPut = new HttpEntity<>(
         * projectFieldAccessor.getFields(), headers);
         *
         * restTemplate.exchange(requestUrl, HttpMethod.PUT, entityPut, responseType);
         *
         * });
         *
         * } catch (HttpClientErrorException ex) { System.out.println(ex.getMessage()); }
         * });
         *
         * return null; };
         */

        // Define workflow steps
        return WorkflowFactory.builder().step(compareMlrunCoreFunctions, functionUrl)
                .step(storeFunctions)
                .build();

        // .step(updateProject)
        // .conditionalStep((List<FunctionFieldAccessor> s) -> s.size() > 0,
        // upsertFunction);
    }

}
