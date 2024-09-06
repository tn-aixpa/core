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
import it.smartcommunitylabdhub.framework.k8s.base.K8sTaskBaseSpec;
import it.smartcommunitylabdhub.framework.k8s.model.K8sTemplate;
import it.smartcommunitylabdhub.framework.k8s.runnables.K8sRunnable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
            this.templates = loadTemplates().values();
        }
    }

    protected Map<String, K8sTemplate<K8sRunnable>> loadTemplates() {
        //load templates if provided
        Map<String, K8sTemplate<K8sRunnable>> results = new HashMap<>();
        if (resourceLoader != null && templateKeys != null) {
            templateKeys.forEach(k -> {
                try {
                    String[] kk = k.split("\\|");
                    if (kk.length == 2) {
                        String key = kk[0];
                        String path = kk[1];
                        //check if we received a bare path and fix
                        if (!path.startsWith("classpath:") && !path.startsWith("file:")) {
                            path = "file:" + kk[1];
                        }

                        // Load as resource and deserialize as template
                        Resource res = resourceLoader.getResource(path);
                        K8sTemplate<K8sRunnable> t = KubernetesMapper.readTemplate(
                            res.getContentAsString(StandardCharsets.UTF_8),
                            K8sRunnable.class
                        );

                        results.put(key, t);
                    }
                } catch (IOException | ClassCastException e) {
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

            if (templates != null && javaType.isInstanceOf(K8sTaskBaseSpec.class)) {
                ObjectNode def = context.createStandardDefinition(javaType, this);
                JsonNode node = Optional
                    .ofNullable(def.get(config.getKeyword(SchemaKeyword.TAG_PROPERTIES)))
                    .map(o -> o.get("profile"))
                    .orElse(null);

                if (node != null) {
                    //set oneOf
                    ArrayNode opts = config.createArrayNode();
                    templates
                        .stream()
                        .map(t -> {
                            ObjectNode o = config.createObjectNode();
                            o.put(config.getKeyword(SchemaKeyword.TAG_CONST), t.getId());
                            if (StringUtils.hasText(t.getName())) {
                                o.put(config.getKeyword(SchemaKeyword.TAG_TITLE), t.getName());
                            }
                            if (StringUtils.hasText(t.getDescription())) {
                                o.put(config.getKeyword(SchemaKeyword.TAG_DESCRIPTION), t.getDescription());
                            }

                            return o;
                        })
                        .forEach(opts::add);

                    //override definition
                    ObjectNode profile = config.createObjectNode();
                    profile
                        .put(
                            config.getKeyword(SchemaKeyword.TAG_TYPE),
                            config.getKeyword(SchemaKeyword.TAG_TYPE_STRING)
                        )
                        .put(config.getKeyword(SchemaKeyword.TAG_TITLE), "fields.profile.title")
                        .put(config.getKeyword(SchemaKeyword.TAG_DESCRIPTION), "fields.profile.description")
                        .set(config.getKeyword(SchemaKeyword.TAG_ONEOF), opts);

                    ((ObjectNode) def.get("properties")).replace("profile", profile);
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
}
