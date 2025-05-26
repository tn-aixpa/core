package it.smartcommunitylabdhub.commons.models.metrics;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NumberOrNumberArrayJacksonDeserializer extends StdDeserializer<NumberOrNumberArray> {

    public NumberOrNumberArrayJacksonDeserializer() {
        super(NumberOrNumberArrayJacksonDeserializer.class);
    }

    @Override
    public NumberOrNumberArray deserialize(JsonParser jp, DeserializationContext ctxt)
        throws IOException, JacksonException {
        JsonNode node = jp.getCodec().readTree(jp);
        if (node.isArray()) {
            List<Double> values = new ArrayList<>();
            for (JsonNode valueNode : node) {
                values.add(valueNode.asDouble());
            }
            return new NumberOrNumberArray(values);
        }
        return new NumberOrNumberArray(node.asDouble());
    }
    /*private Number getValue(JsonNode valueNode) throws IOException {
		 if(valueNode.isDouble() || valueNode.isFloat()) {
				return Double.valueOf(valueNode.asDouble());
		} else if(valueNode.isLong()) {
			return Long.valueOf(valueNode.asLong());
		} else if(valueNode.isInt()) {
			return Long.valueOf(valueNode.asLong());
		} else
			throw new IOException("type not supported");
	}*/

}
