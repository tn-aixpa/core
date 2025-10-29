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

package it.smartcommunitylabdhub.framework.k8s.jackson;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.victools.jsonschema.generator.CustomDefinition;
import com.github.victools.jsonschema.generator.CustomDefinitionProviderV2;
import com.github.victools.jsonschema.generator.SchemaGenerationContext;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfig;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfigBuilder;
import com.github.victools.jsonschema.generator.SchemaKeyword;
import it.smartcommunitylabdhub.framework.k8s.annotations.ConditionalOnKubernetes;
import it.smartcommunitylabdhub.framework.k8s.base.K8sResourceProfileAware;
import it.smartcommunitylabdhub.framework.k8s.model.K8sTemplate;
import it.smartcommunitylabdhub.framework.k8s.runnables.K8sRunnable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@ConditionalOnKubernetes
public class KubernetesModule implements com.github.victools.jsonschema.generator.Module, InitializingBean {

    private List<String> templateKeys;
    private Collection<K8sTemplate<K8sRunnable>> templates = null;

    protected ResourceLoader resourceLoader;

    @Autowired
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Autowired
    public void setTemplateKeys(@Value("${kubernetes.templates}") List<String> templateKeys) {
        this.templateKeys = templateKeys;
    }

    public void setTemplates(Collection<K8sTemplate<K8sRunnable>> templates) {
        this.templates = templates;
    }

    @Override
    public void applyToConfigBuilder(SchemaGeneratorConfigBuilder builder) {
        if (templates != null) {
            //define custom implementation for k8s templates

            builder.forTypesInGeneral().withCustomDefinitionProvider(new TemplateProvider(templates));
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (templateKeys != null && !templateKeys.isEmpty()) {
            this.templates = loadTemplates();
        }
    }

    protected List<K8sTemplate<K8sRunnable>> loadTemplates() {
        //load templates if provided
        List<K8sTemplate<K8sRunnable>> results = new ArrayList<>();
        if (resourceLoader != null && templateKeys != null) {
            templateKeys.forEach(k -> {
                try {
                    String path = k;
                    //check if we received a bare path and fix
                    if (!path.startsWith("classpath:") && !path.startsWith("file:")) {
                        path = "file:" + k;
                    }

                    // Load as resource and deserialize as template
                    Resource res = resourceLoader.getResource(path);
                    K8sTemplate<K8sRunnable> t = KubernetesMapper.readTemplate(
                        res.getContentAsString(StandardCharsets.UTF_8),
                        K8sRunnable.class
                    );

                    results.add(t);
                } catch (IOException | ClassCastException | IllegalArgumentException e) {
                    //skip
                }
            });
        }

        return results;
    }

    public class TemplateProvider implements CustomDefinitionProviderV2 {

        private final Collection<K8sTemplate<K8sRunnable>> templates;

        public TemplateProvider(Collection<K8sTemplate<K8sRunnable>> templates) {
            this.templates = templates;
        }

        @Override
        public CustomDefinition provideCustomSchemaDefinition(ResolvedType javaType, SchemaGenerationContext context) {
            SchemaGeneratorConfig config = context.getGeneratorConfig();

            if (templates != null && javaType.isInstanceOf(K8sResourceProfileAware.class)) {
                ObjectNode def = context.createStandardDefinition(javaType, this);
                JsonNode node = Optional
                    .ofNullable(def.get(config.getKeyword(SchemaKeyword.TAG_PROPERTIES)))
                    .map(o -> o.get("profile"))
                    .orElse(null);

                //add blank as default
                List<OneOfEntry> profiles = Stream
                    .concat(
                        Stream.of(new OneOfEntry("", "", null)),
                        templates.stream().map(t -> new OneOfEntry(t.getId(), t.getName(), t.getDescription()))
                    )
                    .toList();

                if (node != null) {
                    //set oneOf
                    ArrayNode opts = config.createArrayNode();
                    profiles
                        .stream()
                        .map(t -> {
                            ObjectNode o = config.createObjectNode();
                            o.put(config.getKeyword(SchemaKeyword.TAG_CONST), t.id());
                            if (StringUtils.hasText(t.name())) {
                                //DISABLED, using a fake object for display
                                // o.put(config.getKeyword(SchemaKeyword.TAG_TITLE), t.getName());
                            }
                            if (StringUtils.hasText(t.description())) {
                                //DISABLED, unsupported by frontend lib
                                // o.put(config.getKeyword(SchemaKeyword.TAG_DESCRIPTION), t.getDescription());
                            }

                            return o;
                        })
                        .forEach(opts::add);

                    //override definition
                    ObjectNode profile = config.createObjectNode();
                    profile.set(
                        config.getKeyword(SchemaKeyword.TAG_TYPE),
                        config
                            .createArrayNode()
                            .add(config.getKeyword(SchemaKeyword.TAG_TYPE_STRING))
                            .add(config.getKeyword(SchemaKeyword.TAG_TYPE_NULL))
                    );
                    profile
                        .put(config.getKeyword(SchemaKeyword.TAG_TITLE), "fields.profile.title")
                        .put(config.getKeyword(SchemaKeyword.TAG_DESCRIPTION), "fields.profile.description")
                        .put(config.getKeyword(SchemaKeyword.TAG_DEFAULT), "")
                        .set(config.getKeyword(SchemaKeyword.TAG_ONEOF), opts);

                    ((ObjectNode) def.get(config.getKeyword(SchemaKeyword.TAG_PROPERTIES))).replace("profile", profile);

                    //workaround: build a fake object to expose details about template
                    //TODO: remove when frontend lib supports description on enumerables
                    //see https://github.com/rjsf-team/react-jsonschema-form/issues/4214

                    //set oneOf
                    ArrayNode prps = config.createArrayNode();
                    profiles
                        .stream()
                        .map(t -> {
                            ObjectNode o = config.createObjectNode();
                            o.set(
                                "profile",
                                config.createObjectNode().put(config.getKeyword(SchemaKeyword.TAG_CONST), t.id())
                            );
                            ObjectNode e = config
                                .createObjectNode()
                                .put(
                                    config.getKeyword(SchemaKeyword.TAG_TYPE),
                                    config.getKeyword(SchemaKeyword.TAG_TYPE_OBJECT)
                                );
                            if (StringUtils.hasText(t.name())) {
                                e.put(config.getKeyword(SchemaKeyword.TAG_TITLE), t.name());
                            }
                            if (StringUtils.hasText(t.description())) {
                                e.put(config.getKeyword(SchemaKeyword.TAG_DESCRIPTION), t.description());
                            }

                            o.set("template", e);

                            ObjectNode s = config.createObjectNode();
                            s.set(config.getKeyword(SchemaKeyword.TAG_PROPERTIES), o);

                            return s;
                        })
                        .forEach(prps::add);

                    //build deps to expose fake node for details
                    ObjectNode deps = config.createObjectNode();
                    deps.set(config.getKeyword(SchemaKeyword.TAG_ONEOF), prps);

                    def.set("dependencies", config.createObjectNode().set("profile", deps));
                }

                return new CustomDefinition(
                    def,
                    CustomDefinition.DefinitionType.ALWAYS_REF,
                    CustomDefinition.AttributeInclusion.YES
                );
            }
            return null;
        }
    }

    public record OneOfEntry(String id, String name, String description) {}
}
