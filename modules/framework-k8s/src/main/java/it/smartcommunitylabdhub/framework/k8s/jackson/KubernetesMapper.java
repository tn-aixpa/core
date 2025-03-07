package it.smartcommunitylabdhub.framework.k8s.jackson;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLParser;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.kubernetes.client.custom.IntOrString;
import io.kubernetes.client.custom.Quantity;
import io.kubernetes.client.openapi.models.V1CronJob;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.openapi.models.V1Job;
import io.kubernetes.client.openapi.models.V1Service;
import it.smartcommunitylabdhub.commons.jackson.JacksonMapper;
import it.smartcommunitylabdhub.commons.jackson.YamlMapperFactory;
import it.smartcommunitylabdhub.framework.k8s.model.K8sTemplate;
import it.smartcommunitylabdhub.framework.k8s.runnables.K8sRunnable;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;

public class KubernetesMapper {

    //custom object mapper with mixIn for IntOrString
    public static final ObjectMapper OBJECT_MAPPER = JacksonMapper.CUSTOM_OBJECT_MAPPER
        .addMixIn(IntOrString.class, IntOrStringMixin.class)
        .addMixIn(Quantity.class, QuantityMixin.class)
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public static final YAMLFactory YAML_FACTORY = YamlMapperFactory.yamlFactory();
    public static final ObjectMapper YAML_OBJECT_MAPPER = new ObjectMapper(YAML_FACTORY);

    public static final TypeReference<HashMap<String, Serializable>> TYPE_REF = new TypeReference<
        HashMap<String, Serializable>
    >() {};

    static {
        YAML_OBJECT_MAPPER.registerModule(new JavaTimeModule());
        YAML_OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        YAML_OBJECT_MAPPER.setSerializationInclusion(Include.NON_EMPTY);
        YAML_OBJECT_MAPPER.configure(MapperFeature.USE_GETTERS_AS_SETTERS, false);
    }

    public static <T extends K8sRunnable> K8sTemplate<T> readTemplate(String yaml, Class<T> clazz) throws IOException {
        YAMLParser yamlParser = YAML_FACTORY.createParser(yaml);

        K8sTemplate<T> template = new K8sTemplate<>();
        MappingIterator<HashMap<String, Serializable>> values = YAML_OBJECT_MAPPER.readValues(
            yamlParser,
            KubernetesMapper.TYPE_REF
        );
        values.forEachRemaining(map -> {
            if (map.get("kind") != null) {
                //k8s object, match
                String kind = (String) map.get("kind");

                if ("Deployment".equals(kind)) {
                    template.setDeployment(YAML_OBJECT_MAPPER.convertValue(map, V1Deployment.class));
                } else if ("Job".equals(kind)) {
                    template.setJob(YAML_OBJECT_MAPPER.convertValue(map, V1Job.class));
                } else if ("CronJob".equals(kind)) {
                    template.setCronJob(YAML_OBJECT_MAPPER.convertValue(map, V1CronJob.class));
                } else if ("Service".equals(kind)) {
                    template.setService(YAML_OBJECT_MAPPER.convertValue(map, V1Service.class));
                }
            } else if (map.get("id") != null) {
                //base profile
                T profile = YAML_OBJECT_MAPPER.convertValue(map, clazz);
                template.setProfile(profile);

                //read name+description if provided
                template.setName((String) map.getOrDefault("name", map.get("id")));
                template.setDescription((String) map.getOrDefault("description", ""));
            } else {
                //unknown, ignore
            }
        });

        //validate
        if (template.getProfile() == null) {
            throw new IllegalArgumentException("invalid template, missing profile");
        }

        return template;
    }

    private KubernetesMapper() {}
}
