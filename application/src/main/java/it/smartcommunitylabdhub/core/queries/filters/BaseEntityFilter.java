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

package it.smartcommunitylabdhub.core.queries.filters;

import it.smartcommunitylabdhub.commons.models.queries.SearchCriteria;
import it.smartcommunitylabdhub.commons.models.queries.SearchFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.Specification;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BaseEntityFilter<T> implements SearchFilter<T> {

    private List<SearchCriteria<T>> criteria;
    private List<SearchFilter<T>> filters;
    private Condition condition;

    @Override
    public Specification<T> toSpecification() {
        List<Specification<T>> specs = new ArrayList<>();

        Optional
            .ofNullable(criteria)
            .ifPresent(criteria -> {
                specs.addAll(criteria.stream().map(f -> f.toSpecification()).collect(Collectors.toList()));
            });

        Optional
            .ofNullable(filters)
            .ifPresent(filters -> {
                specs.addAll(filters.stream().map(f -> f.toSpecification()).collect(Collectors.toList()));
            });

        if (specs == null || specs.isEmpty()) {
            return null;
        }

        switch (condition) {
            case Condition.and:
                return Specification.allOf(specs);
            case Condition.or:
                return Specification.anyOf(specs);
            default:
                return null;
        }
    }
}
