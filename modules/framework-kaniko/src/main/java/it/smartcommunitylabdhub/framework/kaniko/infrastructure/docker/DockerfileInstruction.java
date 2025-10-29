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

package it.smartcommunitylabdhub.framework.kaniko.infrastructure.docker;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class DockerfileInstruction {

    @NotNull
    private Kind instruction;

    //options as required by the instruction
    //example: --mount=type=cache,target=/root/.cache/go-build
    private String[] opts;

    private String[] args;

    @JsonIgnore
    public String write() {
        StringBuilder sb = new StringBuilder();
        sb.append(instruction.name()).append(" ");
        if (opts != null) {
            sb.append(String.join(",", opts));
        }
        if (args != null) {
            sb.append(String.join(" ", args));
        }

        return sb.toString();
    }

    public static DockerfileInstruction read(String line) {
        if (line != null && !line.isBlank() && !line.trim().startsWith("#")) {
            String[] parts = line.trim().split("\\s+", 2);
            if (parts.length > 0) {
                DockerfileInstruction.Kind kind = DockerfileInstruction.Kind.valueOf(parts[0].toUpperCase());
                String[] args = parts.length > 1 ? parts[1].split("\\s+") : new String[0];
                return DockerfileInstruction.builder().instruction(kind).args(args).build();
            }
        }

        return null;
    }

    public enum Kind {
        ADD,
        ARG,
        CMD,
        COPY,
        ENTRYPOINT,
        ENV,
        EXPOSE,
        FROM,
        HEALTHCHECK,
        LABEL,
        RUN,
        SHELL,
        USER,
        VOLUME,
        WORKDIR,
    }
}
