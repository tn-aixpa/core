package it.smartcommunitylabdhub.framework.k8s.infrastructure.watcher;

import it.smartcommunitylabdhub.commons.config.ApplicationProperties;
import it.smartcommunitylabdhub.framework.k8s.infrastructure.k8s.K8sDeploymentFramework;
import it.smartcommunitylabdhub.framework.k8s.infrastructure.k8s.K8sJobFramework;
import it.smartcommunitylabdhub.framework.k8s.infrastructure.k8s.K8sServeFramework;
import it.smartcommunitylabdhub.framework.k8s.kubernetes.K8sBuilderHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;


@Configuration
@Slf4j
public class K8sWatcherConfig {
    private final K8sJobWatcherService jobWatcher;
    private final K8sDeploymentWatcherService deploymentWatcher;
    private final K8sServeWatcherService serviceWatcher;
    private final ApplicationProperties applicationProperties;

    public K8sWatcherConfig(K8sJobWatcherService jobWatcher, K8sDeploymentWatcherService deploymentWatcher, K8sServeWatcherService serviceWatcher, ApplicationProperties applicationProperties) {
        this.jobWatcher = jobWatcher;
        this.deploymentWatcher = deploymentWatcher;
        this.serviceWatcher = serviceWatcher;
        this.applicationProperties = applicationProperties;
    }

    @PostConstruct
    public void initWatchers() {
        log.info("🔄 Starting Kubernetes Watchers...");

        // Each watcher gets only its corresponding label
        jobWatcher.startWatcher(K8sBuilderHelper.sanitizeNames(applicationProperties.getName()) + "/framework=" + K8sJobFramework.FRAMEWORK);
        deploymentWatcher.startWatcher(K8sBuilderHelper.sanitizeNames(applicationProperties.getName()) + "/framework=" + K8sDeploymentFramework.FRAMEWORK);
        serviceWatcher.startWatcher(K8sBuilderHelper.sanitizeNames(applicationProperties.getName()) + "/framework=" + K8sServeFramework.FRAMEWORK);
    }
}


//@Configuration
//@Slf4j
//public class K8sWatcherConfig {
//    private final K8sAbstractWatcher k8sWatcherService;
//    private final ApplicationProperties applicationProperties;
//
//    public K8sWatcherConfig(K8sAbstractWatcher k8sWatcherService, ApplicationProperties applicationProperties) {
//        this.k8sWatcherService = k8sWatcherService;
//        this.applicationProperties = applicationProperties;
//    }
//
//    @PostConstruct
//    public void initWatchers() {
//        log.info("🔄 Starting Kubernetes Watchers...");
//        List<String> watchers = List.of(
//                K8sBuilderHelper.sanitizeNames(applicationProperties.getName()) + "/framework=" + K8sJobFramework.FRAMEWORK,
//                K8sBuilderHelper.sanitizeNames(applicationProperties.getName()) + "/framework=" + K8sDeploymentFramework.FRAMEWORK,
//                K8sBuilderHelper.sanitizeNames(applicationProperties.getName()) + "/framework=" + K8sServeFramework.FRAMEWORK
//        );
//
//        k8sWatcherService.startMultipleWatchers(watchers);
//    }
//
//
//}