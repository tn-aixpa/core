package it.smartcommunitylabdhub.framework.k8s.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.kubernetes.client.openapi.models.V1ServicePort;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class K8sServiceInfo {

    private String name;
    private String namespace;
    private String type;

    private String clusterIP;
    private String externalName;
    private List<String> externalIps;

    private String ip;
    private String hostname;
    private List<V1ServicePort> ports;

    private String url;
    private List<String> urls;
}
