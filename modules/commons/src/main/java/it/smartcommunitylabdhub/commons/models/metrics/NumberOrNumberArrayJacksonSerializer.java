package it.smartcommunitylabdhub.commons.models.metrics;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class NumberOrNumberArrayJacksonSerializer extends StdSerializer<NumberOrNumberArray> {
	
	public NumberOrNumberArrayJacksonSerializer() {
		super(NumberOrNumberArray.class);
	}
	
	@Override
	public void serialize(NumberOrNumberArray obj, JsonGenerator gen, SerializerProvider provider)
			throws IOException {
		if(obj.getValue() != null) {
			gen.writeNumber(obj.getValue().doubleValue());
		}
		if((obj.getValues() != null) && (obj.getValues().size() > 0)) {
			double[] array = obj.getValues().stream().mapToDouble(n -> n.doubleValue()).toArray();
			gen.writeArray(array, 0, array.length);
		}
	}
	
}
