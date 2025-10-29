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

package it.smartcommunitylabdhub.framework.kaniko.config;

import io.kubernetes.client.openapi.ApiClient;
import it.smartcommunitylabdhub.commons.services.RunnableStore;
import it.smartcommunitylabdhub.framework.k8s.annotations.ConditionalOnKubernetes;
import it.smartcommunitylabdhub.framework.kaniko.infrastructure.k8s.K8sBuildkitFramework;
import it.smartcommunitylabdhub.framework.kaniko.infrastructure.k8s.K8sKanikoFramework;
import it.smartcommunitylabdhub.framework.kaniko.runnables.K8sContainerBuilderRunnable;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ContainerBuilderFrameworkConfig {

    @Bean
    @ConditionalOnKubernetes
    public RunnableStore<K8sContainerBuilderRunnable> k8sKanikoRunnableStoreService(
        RunnableStore.StoreSupplier storeSupplier
    ) {
        return storeSupplier.get(K8sContainerBuilderRunnable.class);
    }

    @Bean
    @ConditionalOnKubernetes
    @ConditionalOnProperty(name = "builder.framework", havingValue = "kaniko", matchIfMissing = false)
    public K8sKanikoFramework k8sKanikoFramework(ApiClient apiClient) {
        return new K8sKanikoFramework(apiClient);
    }

    @Bean
    @ConditionalOnKubernetes
    @ConditionalOnProperty(name = "builder.framework", havingValue = "buildkit", matchIfMissing = false)
    public K8sBuildkitFramework k8sBuildkitFramework(ApiClient apiClient) {
        return new K8sBuildkitFramework(apiClient);
    }
}
