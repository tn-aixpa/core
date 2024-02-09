package it.smartcommunitylabdhub.core.models.validators.utils;

import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;

import java.io.IOException;
import javax.annotation.Nonnull;

public class JSONSchemaValidator {

	JSONSchemaValidator() {}

	public static boolean validateWithSchema(String jsonData, @Nonnull String schemaFileName) {
		try {
			JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
			JsonSchema schema = factory.getJsonSchema(JsonLoader.fromResource(schemaFileName));
			ProcessingReport report = schema.validate(JsonLoader.fromString(jsonData));
			return report.isSuccess();
		} catch (IOException | ProcessingException e) {
			// Handle exceptions
			return false;
		}
	}
}
