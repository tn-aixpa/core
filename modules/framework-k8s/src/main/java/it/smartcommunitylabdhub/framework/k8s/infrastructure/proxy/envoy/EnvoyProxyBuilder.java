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

                                EnvoyConfig.RouteAction routeAction = EnvoyConfig.RouteAction.builder()
                                                .cluster(appServiceName)
                                                .build();

                                EnvoyConfig.Match match = EnvoyConfig.Match.builder()
                                                .prefix("/")
                                                .build();

                                EnvoyConfig.Route route = EnvoyConfig.Route.builder()
                                                .name("")
                                                .match(match)
                                                .route(routeAction)
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

                                EnvoyConfig.TypedConfig routerTypedConfig = EnvoyConfig.TypedConfig.builder()
                                                .type("type.googleapis.com/envoy.extensions.filters.http.router.v3.Router")
                                                .build();

                                EnvoyConfig.HttpFilter httpFilter = EnvoyConfig.HttpFilter.builder()
                                                .name("envoy.filters.http.router")
                                                .typedConfig(routerTypedConfig)
                                                .build();

                                EnvoyConfig.TypedConfig hcmTypedConfig = EnvoyConfig.TypedConfig.builder()
                                                .type("type.googleapis.com/envoy.extensions.filters.network.http_connection_manager.v3.HttpConnectionManager")
                                                .statPrefix("mainapp_sidecar_hcm_filter_" + i)
                                                .httpFilters(List.of(httpFilter))
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
