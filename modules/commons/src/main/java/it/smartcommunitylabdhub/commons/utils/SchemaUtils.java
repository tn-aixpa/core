package it.smartcommunitylabdhub.commons.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.victools.jsonschema.generator.OptionPreset;
import com.github.victools.jsonschema.generator.SchemaGenerator;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfigBuilder;
import com.github.victools.jsonschema.generator.SchemaVersion;
import it.smartcommunitylabdhub.commons.models.specs.Spec;

public final class SchemaUtils {

    public static final SchemaGenerator GENERATOR;

    static {
        SchemaGeneratorConfigBuilder configBuilder = new SchemaGeneratorConfigBuilder(
            SchemaVersion.DRAFT_2020_12,
            OptionPreset.PLAIN_JSON
        );

        GENERATOR = new SchemaGenerator(configBuilder.build());
    }

    public static JsonNode schema(Class<? extends Spec> clazz) {
        return GENERATOR.generateSchema(clazz);
    }

    private SchemaUtils() {}
}
