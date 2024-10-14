package it.smartcommunitylabdhub.framework.k8s.infrastructure.proxy.envoy;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import it.smartcommunitylabdhub.framework.k8s.exceptions.K8sFrameworkException;
import it.smartcommunitylabdhub.framework.k8s.objects.CorePort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import it.smartcommunitylabdhub.framework.k8s.annotations.ConditionalOnKubernetes;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Component
@Slf4j
@ConditionalOnKubernetes
public class EnvoyProxyBuilder {

    public String getConfiguration(List<CorePort> corePorts)
            throws K8sFrameworkException {
        try {

            List<EnvoyConfig.Listener> listeners = new ArrayList<>();
            List<EnvoyConfig.Cluster> clusters = new ArrayList<>();

            IntStream.range(0, corePorts.size()).forEach(i -> {
                int sidecarPort = Integer.parseInt("5" + corePorts.get(i).targetPort()); // Add prefix 5
                // to the port

                String appServiceName = "mainapp_service_" + i;
                EnvoyConfig.SocketAddress listenerSocketAddress = EnvoyConfig.SocketAddress.builder()
                        .address("0.0.0.0")
                        .portValue(sidecarPort)
                        .build();

                EnvoyConfig.Address listenerAddress = EnvoyConfig.Address.builder()
                        .socketAddress(listenerSocketAddress)
                        .build();

                EnvoyConfig.SocketAddress clusterSocketAddress = EnvoyConfig.SocketAddress.builder()
                        .address("127.0.0.1")
                        .portValue(corePorts.get(i).targetPort())
                        .build();

                EnvoyConfig.Address clusterAddress = EnvoyConfig.Address.builder()
                        .socketAddress(clusterSocketAddress)
                        .build();

                EnvoyConfig.Endpoint clusterEndpoint = EnvoyConfig.Endpoint.builder()
                        .address(clusterAddress)
                        .build();

                EnvoyConfig.LbEndpoint lbEndpoint = EnvoyConfig.LbEndpoint.builder()
                        .endpoint(clusterEndpoint)
                        .build();

                EnvoyConfig.EndpointGroup endpointGroup = EnvoyConfig.EndpointGroup.builder()
                        .lbEndpoints(List.of(lbEndpoint))
                        .build();

                EnvoyConfig.LoadAssignment loadAssignment = EnvoyConfig.LoadAssignment.builder()
                        .clusterName(appServiceName)
                        .endpoints(List.of(endpointGroup))
                        .build();

                EnvoyConfig.Cluster cluster = EnvoyConfig.Cluster.builder()
                        .name(appServiceName)
                        .type("STRICT_DNS")
                        .lbPolicy("ROUND_ROBIN")
                        .loadAssignment(loadAssignment)
                        .build();


                // Clusters zipkin CONFIGURATION

                EnvoyConfig.SocketAddress clusterSocketAddressZipkin = EnvoyConfig.SocketAddress.builder()
                        .address("zipkin.default.svc.cluster.local")
                        .portValue(9411)
                        .build();

                EnvoyConfig.Address clusterAddressZipkin = EnvoyConfig.Address.builder()
                        .socketAddress(clusterSocketAddressZipkin)
                        .build();

                EnvoyConfig.Endpoint clusterEndpointZipkin = EnvoyConfig.Endpoint.builder()
                        .address(clusterAddressZipkin)
                        .build();

                EnvoyConfig.LbEndpoint lbEndpointZipkin = EnvoyConfig.LbEndpoint.builder()
                        .endpoint(clusterEndpointZipkin)
                        .build();

                EnvoyConfig.EndpointGroup endpointGroupZipkin = EnvoyConfig.EndpointGroup.builder()
                        .lbEndpoints(List.of(lbEndpointZipkin))
                        .build();

                EnvoyConfig.LoadAssignment loadAssignmentZipkin = EnvoyConfig.LoadAssignment.builder()
                        .clusterName("zipkin_cluster")
                        .endpoints(List.of(endpointGroupZipkin))
                        .build();

                EnvoyConfig.Cluster zipkinCluster = EnvoyConfig.Cluster.builder()
                        .name("zipkin_cluster")
                        .connectTimeout("1s")
                        .type("STRICT_DNS")
                        .lbPolicy("ROUND_ROBIN")
                        .loadAssignment(loadAssignmentZipkin)
                        .build();

                EnvoyConfig.RouteAction routeAction = EnvoyConfig.RouteAction.builder()
                        .cluster(appServiceName)
                        .build();

                EnvoyConfig.Match match = EnvoyConfig.Match.builder()
                        .prefix("/")
                        .build();

                EnvoyConfig.ResponseHeadersToAdd requestId = EnvoyConfig.ResponseHeadersToAdd.builder()
                        .header(
                                EnvoyConfig.Header.builder()
                                        .key("x-request-id")
                                        .value("%REQ(x-request-id)%")
                                        .build()
                        ).build();

                EnvoyConfig.ResponseHeadersToAdd xb3TraceId = EnvoyConfig.ResponseHeadersToAdd.builder()
                        .header(
                                EnvoyConfig.Header.builder()
                                        .key("x-b3-traceid")
                                        .value("%REQ(x-b3-traceid)%")
                                        .build()
                        ).build();


                EnvoyConfig.Route route = EnvoyConfig.Route.builder()
                        .name("")
                        .match(match)
                        .route(routeAction)
                        .responseHeadersToAdd(
                                List.of(requestId, xb3TraceId))
                        .build();

                EnvoyConfig.VirtualHost virtualHost = EnvoyConfig.VirtualHost.builder()
                        .name("mainapp_sidecar_virtual_host_" + i)
                        .domains(List.of("*"))
                        .routes(List.of(route))
                        .build();

                EnvoyConfig.RouteConfig routeConfig = EnvoyConfig.RouteConfig.builder()
                        .name("mainapp_sidecar_http_route_config_" + i)
                        .virtualHosts(List.of(virtualHost))
                        .build();

                String luaCode = "function envoy_on_request(request_handle) local request_headers = request_handle:headers() for key, value in request_headers:forEach() do request_handle:logInfo(\"Request Header: \" .. key .. \": \" .. value) end local request_body = request_handle:body():getBytes(0, request_handle:body():length()) if #request_body > 0 then request_handle:logInfo(\"Request Body: \" .. request_body) end end function envoy_on_response(response_handle) local response_headers = response_handle:headers() for key, value in response_headers:forEach() do response_handle:logInfo(\"Response Header: \" .. key .. \": \" .. value) end local response_body = response_handle:body():getBytes(0, response_handle:body():length()) if #response_body > 0 then response_handle:logInfo(\"Response Body: \" .. response_body) end end function envoy_on_log() end";
                EnvoyConfig.TypedConfig routerTypedConfigLua = EnvoyConfig.TypedConfig.builder()
                        .type("type.googleapis.com/envoy.extensions.filters.http.lua.v3.Lua")
                        .inlineCode(luaCode)
                        .build();

                EnvoyConfig.TypedConfig routerTypedConfig = EnvoyConfig.TypedConfig.builder()
                        .type("type.googleapis.com/envoy.extensions.filters.http.router.v3.Router")
                        .build();

                EnvoyConfig.HttpFilter httpFilterLua = EnvoyConfig.HttpFilter.builder()
                        .name("envoy.filters.http.lua")
                        .typedConfig(routerTypedConfigLua)
                        .build();


                EnvoyConfig.HttpFilter httpFilter = EnvoyConfig.HttpFilter.builder()
                        .name("envoy.filters.http.router")
                        .typedConfig(routerTypedConfig)
                        .build();

                EnvoyConfig.Provider provider = EnvoyConfig.Provider.builder()
                        .name("envoy.tracers.zipkin")
                        .typedConfig(EnvoyConfig.TypedConfig.builder()
                                .type("type.googleapis.com/envoy.config.trace.v3.ZipkinConfig")
                                .collectorCluster("zipkin_cluster")
                                .collectorEndpoint("/api/v2/spans")
                                .collectorEndpointVersion("HTTP_JSON").build()).build();

                EnvoyConfig.Tracing tracing = EnvoyConfig.Tracing.builder()
                        .provider(provider).build();


                EnvoyConfig.TypedConfig hcmTypedConfig = EnvoyConfig.TypedConfig.builder()
                        .type("type.googleapis.com/envoy.extensions.filters.network.http_connection_manager.v3.HttpConnectionManager")
                        .statPrefix("mainapp_sidecar_hcm_filter_" + i)
                        .codecType("AUTO")
                        .httpFilters(List.of(httpFilterLua,httpFilter))
                        .tracing(tracing)
                        .routeConfig(routeConfig)
                        .build();

                EnvoyConfig.Filter hcmFilter = EnvoyConfig.Filter.builder()
                        .name("envoy.filters.network.http_connection_manager")
                        .typedConfig(hcmTypedConfig)
                        .build();

                EnvoyConfig.FilterChain filterChain = EnvoyConfig.FilterChain.builder()
                        .filters(List.of(hcmFilter))
                        .build();

                EnvoyConfig.Listener listener = EnvoyConfig.Listener.builder()
                        .name("mainapp_sidecar_listener_" + i)
                        .address(listenerAddress)
                        .filterChains(List.of(filterChain))
                        .build();

                listeners.add(listener);
                clusters.add(cluster);
                clusters.add(zipkinCluster);
            });

            EnvoyConfig.StaticResources staticResources = EnvoyConfig.StaticResources.builder()
                    .listeners(listeners)
                    .clusters(clusters)
                    .build();

            EnvoyConfig.Admin admin = EnvoyConfig.Admin.builder().accessLogPath("/tmp/admin_access.log")
                    .address(EnvoyConfig.Address.builder()
                            .socketAddress(EnvoyConfig.SocketAddress
                                    .builder()
                                    .address("0.0.0.0")
                                    .portValue(9901)
                                    .build())
                            .build())
                    .build();

            EnvoyConfig envoyConfig = EnvoyConfig.builder()
                    .staticResources(staticResources)
                    .admin(admin)
                    .build();

            ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
            yamlMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

            String yamlString = yamlMapper.writeValueAsString(envoyConfig);
            log.info(yamlString);

            return yamlString;
        } catch (JsonProcessingException e) {
            throw new K8sFrameworkException(e.getMessage());
        }
    }
}
