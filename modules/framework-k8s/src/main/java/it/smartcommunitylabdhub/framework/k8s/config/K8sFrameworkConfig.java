/*
 * SPDX-FileCopyrightText: © 2025 DSLab - Fondazione Bruno Kessler
 *
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * Copyright 2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package it.smartcommunitylabdhub.framework.k8s.config;

import io.kubernetes.client.openapi.ApiClient;
import it.smartcommunitylabdhub.commons.services.RunnableStore;
import it.smartcommunitylabdhub.framework.k8s.annotations.ConditionalOnKubernetes;
import it.smartcommunitylabdhub.framework.k8s.infrastructure.k8s.K8sCRFramework;
import it.smartcommunitylabdhub.framework.k8s.infrastructure.k8s.K8sDeploymentFramework;
import it.smartcommunitylabdhub.framework.k8s.infrastructure.k8s.K8sJobFramework;
import it.smartcommunitylabdhub.framework.k8s.infrastructure.k8s.K8sServeFramework;
import it.smartcommunitylabdhub.framework.k8s.listeners.K8sCRListener;
import it.smartcommunitylabdhub.framework.k8s.listeners.K8sDeploymentListener;
import it.smartcommunitylabdhub.framework.k8s.listeners.K8sJobListener;
import it.smartcommunitylabdhub.framework.k8s.listeners.K8sRunnableListener;
import it.smartcommunitylabdhub.framework.k8s.listeners.K8sServeListener;
import it.smartcommunitylabdhub.framework.k8s.runnables.K8sCRRunnable;
import it.smartcommunitylabdhub.framework.k8s.runnables.K8sDeploymentRunnable;
import it.smartcommunitylabdhub.framework.k8s.runnables.K8sJobRunnable;
import it.smartcommunitylabdhub.framework.k8s.runnables.K8sServeRunnable;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class K8sFrameworkConfig {

    @Bean
    @ConditionalOnKubernetes
    @ConfigurationProperties(prefix = "kubernetes")
    public KubernetesProperties kubernetesProperties() {
        return new KubernetesProperties();
    }

    @Bean
    @ConditionalOnKubernetes
    public RunnableStore<K8sServeRunnable> k8sServeRunnableStoreService(RunnableStore.StoreSupplier storeSupplier) {
        return storeSupplier.get(K8sServeRunnable.class);
    }

    @Bean
    @ConditionalOnKubernetes
    public RunnableStore<K8sDeploymentRunnable> k8sDeploymentRunnableStoreService(
        RunnableStore.StoreSupplier storeSupplier
    ) {
        return storeSupplier.get(K8sDeploymentRunnable.class);
    }

    @Bean
    @ConditionalOnKubernetes
    public RunnableStore<K8sJobRunnable> k8sjobRunnableStoreService(RunnableStore.StoreSupplier storeSupplier) {
        return storeSupplier.get(K8sJobRunnable.class);
    }

    @Bean
    @ConditionalOnKubernetes
    public RunnableStore<K8sCRRunnable> k8sCRRunnableStoreService(RunnableStore.StoreSupplier storeSupplier) {
        return storeSupplier.get(K8sCRRunnable.class);
    }

    @Bean
    @ConditionalOnKubernetes
    public K8sJobFramework k8sJobFramework(ApiClient apiClient) {
        return new K8sJobFramework(apiClient);
    }

    @Bean
    @ConditionalOnKubernetes
    public K8sDeploymentFramework k8sDeploymentFramework(ApiClient apiClient) {
        return new K8sDeploymentFramework(apiClient);
    }

    @Bean
    @ConditionalOnKubernetes
    public K8sServeFramework k8sServeFramework(ApiClient apiClient) {
        return new K8sServeFramework(apiClient);
    }

    @Bean
    @ConditionalOnKubernetes
    public K8sCRFramework k8sCRFramework(ApiClient apiClient) {
        return new K8sCRFramework(apiClient);
    }

    @Bean
    @ConditionalOnKubernetes
    public K8sRunnableListener<K8sJobRunnable> k8sJobRunnableListener(
        K8sJobFramework jobFramework,
        RunnableStore<K8sJobRunnable> store
    ) {
        return new K8sJobListener(jobFramework, store);
    }

    @Bean
    @ConditionalOnKubernetes
    public K8sRunnableListener<K8sDeploymentRunnable> k8sDeploymentRunnableListener(
        K8sDeploymentFramework deploymentFramework,
        RunnableStore<K8sDeploymentRunnable> store
    ) {
        return new K8sDeploymentListener(deploymentFramework, store);
    }

    @Bean
    @ConditionalOnKubernetes
    public K8sRunnableListener<K8sCRRunnable> k8sCRRunnableListener(
        K8sCRFramework crFramework,
        RunnableStore<K8sCRRunnable> store
    ) {
        return new K8sCRListener(crFramework, store);
    }

    @Bean
    @ConditionalOnKubernetes
    public K8sRunnableListener<K8sServeRunnable> k8sServeRunnableListener(
        K8sServeFramework serveFramework,
        RunnableStore<K8sServeRunnable> store
    ) {
        return new K8sServeListener(serveFramework, store);
    }
}
