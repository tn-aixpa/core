package it.smartcommunitylabdhub.commons.jackson.definitions;

import com.github.victools.jsonschema.generator.CustomDefinition;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfigBuilder;
import io.swagger.v3.oas.annotations.media.Schema;
import it.smartcommunitylabdhub.commons.jackson.mixins.SerializableMixin;
import java.io.Serializable;
import org.springframework.util.StringUtils;

public class SerializableDefinitionsModule implements com.github.victools.jsonschema.generator.Module {

    @Override
    public void applyToConfigBuilder(SchemaGeneratorConfigBuilder builder) {
        builder
            .forTypesInGeneral()
            .withCustomDefinitionProvider((javaType, context) -> {
                //redefine Serializable via mixin with annotations, and inline
                return Serializable.class.equals(javaType.getErasedType())
                    ? new CustomDefinition(
                        context.createDefinition(context.getTypeContext().resolve(SerializableMixin.class)),
                        CustomDefinition.DefinitionType.STANDARD,
                        CustomDefinition.AttributeInclusion.YES
                    )
                    : null;
            })
            .withCustomDefinitionProvider((javaType, context) -> {
                Schema sa = javaType.getErasedType().getAnnotation(Schema.class);
                if (sa != null && sa.implementation() != Void.class) {
                    //override with implementation type, inline
                    return new CustomDefinition(
                        context.createDefinition(context.getTypeContext().resolve(sa.implementation())),
                        CustomDefinition.DefinitionType.STANDARD,
                        CustomDefinition.AttributeInclusion.YES
                    );
                }

                return null;
            })
            .withTypeAttributeOverride((node, scope, context) -> {
                //for custom defined overrides also inject props from schema, since those are skipper by other modules
                if (SerializableMixin.class.getPackage().equals(scope.getType().getErasedType().getPackage())) {
                    Schema sa = scope.getType().getErasedType().getAnnotation(Schema.class);
                    if (sa != null) {
                        if (StringUtils.hasText(sa.title())) {
                            node.put("title", sa.title());
                        }
                        if (StringUtils.hasText(sa.description())) {
                            node.put("description", sa.description());
                        }
                        if (StringUtils.hasText(sa.defaultValue())) {
                            node.put("defaultValue", sa.defaultValue());
                        }
                    }

                    //also override title for array
                    if (scope.getType().isArray()) {
                        node.put("title", "array");
                    }
                }
            });
    }
}
