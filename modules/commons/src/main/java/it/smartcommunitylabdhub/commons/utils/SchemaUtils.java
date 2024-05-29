package it.smartcommunitylabdhub.commons.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.victools.jsonschema.generator.ConfigFunction;
import com.github.victools.jsonschema.generator.FieldScope;
import com.github.victools.jsonschema.generator.Option;
import com.github.victools.jsonschema.generator.OptionPreset;
import com.github.victools.jsonschema.generator.SchemaGenerator;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfigBuilder;
import com.github.victools.jsonschema.generator.SchemaVersion;
import com.github.victools.jsonschema.module.jackson.JacksonModule;
import com.github.victools.jsonschema.module.jackson.JacksonOption;
import com.github.victools.jsonschema.module.jakarta.validation.JakartaValidationModule;
import com.github.victools.jsonschema.module.jakarta.validation.JakartaValidationOption;
import com.github.victools.jsonschema.module.swagger2.Swagger2Module;
import it.smartcommunitylabdhub.commons.jackson.introspect.JsonSchemaAnnotationIntrospector;
import it.smartcommunitylabdhub.commons.models.specs.Spec;

//TODO refactor into a factory
public final class SchemaUtils {

    public static final String FIELDS_PREFIX = "fields.";

    public static final SchemaGenerator GENERATOR;

    static {
        ObjectMapper schemaMapper = new ObjectMapper()
            .setAnnotationIntrospector(new JsonSchemaAnnotationIntrospector());

        JacksonModule jacksonModule = new JacksonModule(
            JacksonOption.IGNORE_TYPE_INFO_TRANSFORM,
            JacksonOption.RESPECT_JSONPROPERTY_ORDER
            // JacksonOption.INCLUDE_ONLY_JSONPROPERTY_ANNOTATED_METHODS
        );
        JakartaValidationModule jakartaModule = new JakartaValidationModule(
            JakartaValidationOption.NOT_NULLABLE_FIELD_IS_REQUIRED,
            JakartaValidationOption.INCLUDE_PATTERN_EXPRESSIONS
        );
        Swagger2Module swagger2Module = new Swagger2Module();

        SchemaGeneratorConfigBuilder configBuilder = new SchemaGeneratorConfigBuilder(
            schemaMapper,
            SchemaVersion.DRAFT_2020_12,
            OptionPreset.PLAIN_JSON
        )
            .with(jacksonModule)
            .with(jakartaModule)
            .with(swagger2Module)
            //options
            .with(Option.DEFINITIONS_FOR_ALL_OBJECTS)
            .with(Option.MAP_VALUES_AS_ADDITIONAL_PROPERTIES)
            .with(Option.PLAIN_DEFINITION_KEYS)
            .with(Option.ENUM_KEYWORD_FOR_SINGLE_VALUES)
            .with(Option.FLATTENED_ENUMS_FROM_TOSTRING)
            .with(Option.PUBLIC_NONSTATIC_FIELDS);
        //avoid fields without getters (ex. unwrapped fields)
        //DISABLED: breaks records
        // .without(Option.NONPUBLIC_NONSTATIC_FIELDS_WITHOUT_GETTERS);

        //auto-generate titles and descriptions via a fixed naming schema
        ConfigFunction<FieldScope, String> titleFromNameResolver = scope -> {
            if (scope.isFakeContainerItemScope()) {
                return FIELDS_PREFIX + scope.getDeclaredName() + ".item.title";
            }
            return FIELDS_PREFIX + scope.getDeclaredName() + ".title";
        };
        ConfigFunction<FieldScope, String> descriptionFromNameResolver = scope -> {
            if (scope.isFakeContainerItemScope()) {
                return FIELDS_PREFIX + scope.getDeclaredName() + ".item.description";
            }
            return FIELDS_PREFIX + scope.getDeclaredName() + ".description";
        };

        configBuilder
            .forFields()
            .withTitleResolver(titleFromNameResolver)
            .withDescriptionResolver(descriptionFromNameResolver);

        GENERATOR = new SchemaGenerator(configBuilder.build());
    }

    public static JsonNode schema(Class<? extends Spec> clazz) {
        return GENERATOR.generateSchema(clazz);
    }

    private SchemaUtils() {}
}
