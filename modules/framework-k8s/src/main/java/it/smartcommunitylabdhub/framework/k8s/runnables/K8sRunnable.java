package it.smartcommunitylabdhub.framework.k8s.runnables;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.smartcommunitylabdhub.commons.infrastructure.RunRunnable;
import it.smartcommunitylabdhub.commons.infrastructure.SecuredRunnable;
import it.smartcommunitylabdhub.framework.k8s.model.ContextRef;
import it.smartcommunitylabdhub.framework.k8s.model.ContextSource;
import it.smartcommunitylabdhub.framework.k8s.objects.CoreAffinity;
import it.smartcommunitylabdhub.framework.k8s.objects.CoreEnv;
import it.smartcommunitylabdhub.framework.k8s.objects.CoreLabel;
import it.smartcommunitylabdhub.framework.k8s.objects.CoreLog;
import it.smartcommunitylabdhub.framework.k8s.objects.CoreMetric;
import it.smartcommunitylabdhub.framework.k8s.objects.CoreNodeSelector;
import it.smartcommunitylabdhub.framework.k8s.objects.CoreResource;
import it.smartcommunitylabdhub.framework.k8s.objects.CoreToleration;
import it.smartcommunitylabdhub.framework.k8s.objects.CoreVolume;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.CredentialsContainer;

@SuperBuilder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class K8sRunnable implements RunRunnable, SecuredRunnable, CredentialsContainer {

    private String id;

    private String project;

    private String runtime;

    private String task;

    private String image;

    private String command;

    private String[] args;

    private List<CoreEnv> envs;

    // mapping secret name to the list of keys to of the secret to use
    private Map<String, Set<String>> secrets;

    private CoreResource resources;

    private List<CoreVolume> volumes;

    @JsonProperty("node_selector")
    private List<CoreNodeSelector> nodeSelector;

    private CoreAffinity affinity;

    private List<CoreToleration> tolerations;

    private String runtimeClass;

    private String priorityClass;

    private List<CoreLabel> labels;

    private String template;

    private String state;

    private Map<String, Serializable> results;

    @JsonIgnore
    private List<CoreLog> logs;

    @JsonIgnore
    private List<CoreMetric> metrics;

    private AbstractAuthenticationToken credentials;

    @JsonProperty("context_refs")
    private List<ContextRef> contextRefs;

    @JsonProperty("context_sources")
    private List<ContextSource> contextSources;

    @Override
    public String getFramework() {
        return "k8s";
    }

    @Override
    public void eraseCredentials() {
        this.credentials = null;
    }

    @Override
    public void setCredentials(Serializable credentials) {
        if (credentials instanceof AbstractAuthenticationToken) {
            this.credentials = (AbstractAuthenticationToken) credentials;
        }
    }
}
