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

package it.smartcommunitylabdhub.components.cloud;

import it.smartcommunitylabdhub.commons.models.base.BaseDTO;
import it.smartcommunitylabdhub.events.EntityAction;
import org.springframework.context.ApplicationEvent;
import org.springframework.core.ResolvableType;
import org.springframework.core.ResolvableTypeProvider;
import org.springframework.util.Assert;

public class CloudEntityEvent<T extends BaseDTO> extends ApplicationEvent implements ResolvableTypeProvider {

    private Class<T> clazz;
    private final EntityAction action;
    private final T dto;

    public CloudEntityEvent(T dto, Class<T> clazz, EntityAction action) {
        super(dto);
        Assert.notNull(action, "action can not be null");
        Assert.notNull(clazz, "class is required");
        this.action = action;
        this.dto = dto;
        this.clazz = clazz;
    }

    public T getDto() {
        return dto;
    }

    public Class<T> getClazz() {
        return clazz;
    }

    public EntityAction getAction() {
        return action;
    }

    @Override
    public ResolvableType getResolvableType() {
        return ResolvableType.forClassWithGenerics(getClass(), ResolvableType.forInstance(this.dto));
    }
}
