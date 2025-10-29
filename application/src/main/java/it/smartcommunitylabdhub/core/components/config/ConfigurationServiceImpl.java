/*
 * SPDX-FileCopyrightText: © 2025 DSLab - Fondazione Bruno Kessler
 *
 * SPDX-License-Identifier: Apache-2.0
 */

/**
 * Copyright 2025 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.smartcommunitylabdhub.core.components.config;

import it.smartcommunitylabdhub.commons.infrastructure.Configuration;
import it.smartcommunitylabdhub.commons.infrastructure.ConfigurationProvider;
import it.smartcommunitylabdhub.commons.services.ConfigurationService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ConfigurationServiceImpl implements ConfigurationService {

    @Autowired
    private List<ConfigurationProvider> providers;

    public List<Configuration> getConfigurations() {
        return providers.stream().map(ConfigurationProvider::getConfig).filter(c -> c != null).toList();
    }
}
