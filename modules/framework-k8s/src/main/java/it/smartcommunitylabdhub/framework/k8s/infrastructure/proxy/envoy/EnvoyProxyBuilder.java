package it.smartcommunitylabdhub.framework.k8s.infrastructure.proxy.envoy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import it.smartcommunitylabdhub.framework.k8s.exceptions.K8sFrameworkException;
import it.smartcommunitylabdhub.framework.k8s.infrastructure.proxy.envoy.fields.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import it.smartcommunitylabdhub.framework.k8s.annotations.ConditionalOnKubernetes;


import java.util.List;

@Component
@Slf4j
@ConditionalOnKubernetes
public class EnvoyProxyBuilder {

    public String getConfiguration(
            String sidecarIp, Integer sidecarPort, String serviceIp, Integer servicePort
    ) throws K8sFrameworkException {
        try {
            StaticResources staticResources = StaticResources.builder()
                    .listeners(List.of(
                            Listener.builder()
                                    .name("core_sidecar_listener")
                                    .address(Address.builder()
                                            .socketAddress(SocketAddress.builder()
                                                    .address(sidecarIp)
                                                    .portValue(sidecarPort)
                                                    .build())
                                            .build())
                                    .filterChains(List.of(
                                            FilterChain.builder()
                                                    .filters(List.of(
                                                            Filter.builder()
                                                                    .name("envoy.filters.network.http_connection_manager")
                                                                    .typedConfig(TypedConfig.builder()
                                                                            .type("type.googleapis.com/envoy.extensions.filters.network.http_connection_manager.v3.HttpConnectionManager")
                                                                            .httpConnectionManager(HttpConnectionManager.builder()
                                                                                    .statPrefix("core_sidecar_hcm_filter")
                                                                                    .httpFilters(List.of(
                                                                                            HttpFilter.builder()
                                                                                                    .name("envoy.filters.http.router")
                                                                                                    .typedConfig(TypedConfig.builder()
                                                                                                            .type("type.googleapis.com/envoy.extensions.filters.http.router.v3.Router")
                                                                                                            .build())
                                                                                                    .build()))
                                                                                    .routeConfig(RouteConfig.builder()
                                                                                            .name("core_sidecar_http_route_config")
                                                                                            .virtualHosts(List.of(
                                                                                                    VirtualHost.builder()
                                                                                                            .name("core_sidecar_virtual_host")
                                                                                                            .domains(List.of("*"))
                                                                                                            .routes(List.of(
                                                                                                                    Route.builder()
                                                                                                                            .match(Match.builder()
                                                                                                                                    .prefix("/")
                                                                                                                                    .build())
                                                                                                                            .routeAction(RouteAction.builder()
                                                                                                                                    .cluster("core_service")
                                                                                                                                    .build())
                                                                                                                            .build()))
                                                                                                            .build()))
                                                                                            .build())
                                                                                    .build())
                                                                            .build())
                                                                    .build()))
                                                    .build()))
                                    .build()))
                    .clusters(List.of(
                            Cluster.builder()
                                    .name("core_service")
                                    .type("STRICT_DNS")
                                    .lbPolicy("ROUND_ROBIN")
                                    .loadAssignment(LoadAssignment.builder()
                                            .clusterName("core_service")
                                            .endpoints(List.of(
                                                    Endpoint.builder()
                                                            .address(Address.builder()
                                                                    .socketAddress(SocketAddress.builder()
                                                                            .address(serviceIp)
                                                                            .portValue(servicePort)
                                                                            .build())
                                                                    .build())
                                                            .build()))
                                            .build())
                                    .build()))
                    .build();

            // Convert StaticResources to YAML
            ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
            String yamlString = yamlMapper.writeValueAsString(staticResources);

            // Print the YAML string ( test )
            System.out.println(yamlString);
            return yamlString;
        } catch (JsonProcessingException e) {
            throw new K8sFrameworkException(e.getMessage());
        }
    }
}
