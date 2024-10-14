package it.smartcommunitylabdhub.framework.k8s.infrastructure.proxy.envoy;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class EnvoyConfig {
    @JsonProperty("static_resources")
    private StaticResources staticResources;
    private Admin admin;

    @Data
    @Builder
    public static class Admin {
        @JsonProperty("access_log_path")
        private String accessLogPath;
        private Address address;
    }

    @Data
    @Builder
    public static class StaticResources {
        private List<Listener> listeners;
        private List<Cluster> clusters;
    }

    @Data
    @Builder
    public static class Listener {
        private String name;
        private Address address;

        @JsonProperty("filter_chains")
        private List<FilterChain> filterChains;
    }

    @Data
    @Builder
    public static class Address {
        @JsonProperty("socket_address")
        private SocketAddress socketAddress;
    }

    @Data
    @Builder
    public static class SocketAddress {
        private String address;

        @JsonProperty("port_value")
        private int portValue;
    }

    @Data
    @Builder
    public static class FilterChain {
        private List<Filter> filters;
    }

    @Data
    @Builder
    public static class Filter {
        private String name;

        @JsonProperty("typed_config")
        private TypedConfig typedConfig;
    }


    @Data
    @Builder
    public static class Provider {

        private String name;

        @JsonProperty("typed_config")
        private TypedConfig typedConfig;
    }

    @Data
    @Builder
    public static class Tracing {
        private Provider provider;
    }

    @Data
    @Builder
    public static class TypedConfig {
        @JsonProperty("@type")
        private String type;

        @JsonProperty("stat_prefix")
        private String statPrefix;

        @JsonProperty("codec_type")
        private String codecType;

        @JsonProperty("http_filters")
        private List<HttpFilter> httpFilters;

        @JsonProperty("tracing")
        private Tracing tracing;

        @JsonProperty("route_config")
        private RouteConfig routeConfig;

        @JsonProperty("collector_cluster")
        private String collectorCluster;

        @JsonProperty("collector_endpoint")
        private String collectorEndpoint;

        @JsonProperty("collector_endpoint_version")
        private String collectorEndpointVersion;

        @JsonProperty("inline_code")
        private String inlineCode;

    }

    @Data
    @Builder
    public static class HttpFilter {
        private String name;

        @JsonProperty("typed_config")
        private TypedConfig typedConfig;
    }

    @Data
    @Builder
    public static class RouteConfig {
        private String name;

        @JsonProperty("virtual_hosts")
        private List<VirtualHost> virtualHosts;
    }

    @Data
    @Builder
    public static class VirtualHost {
        private String name;
        private List<String> domains;
        private List<Route> routes;
    }



    @Data
    @Builder
    public static class Header {
        private String key;
        private String value;
    }

    @Data
    @Builder
    public static class ResponseHeadersToAdd {
        private Header header;
    }

    @Data
    @Builder
    public static class RequestHeadersToAdd {
        private Header header;
    }

    @Data
    @Builder
    public static class Route {
        private String name;
        private Match match;
        private RouteAction route;

        @JsonProperty("response_headers_to_add")
        private List<ResponseHeadersToAdd> responseHeadersToAdd;

        @JsonProperty("request_headers_to_add")
        private List<RequestHeadersToAdd> requestHeadersToAdd;

    }

    @Data
    @Builder
    public static class Match {
        private String prefix;
    }

    @Data
    @Builder
    public static class RouteAction {
        private String cluster;
    }

    @Data
    @Builder
    public static class Cluster {
        private String name;

        @JsonProperty("type")
        private String type;

        @JsonProperty("lb_policy")
        private String lbPolicy;

        @JsonProperty("connect_timeout")
        private String connectTimeout;

        @JsonProperty("load_assignment")
        private LoadAssignment loadAssignment;
    }

    @Data
    @Builder
    public static class LoadAssignment {
        @JsonProperty("cluster_name")
        private String clusterName;
        private List<EndpointGroup> endpoints;
    }

    @Data
    @Builder
    public static class EndpointGroup {
        @JsonProperty("lb_endpoints")
        private List<LbEndpoint> lbEndpoints;
    }

    @Data
    @Builder
    public static class LbEndpoint {
        private Endpoint endpoint;
    }

    @Data
    @Builder
    public static class Endpoint {
        private Address address;
    }
}
