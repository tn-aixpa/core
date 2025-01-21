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
			if((obj.getValue() instanceof Double) || (obj.getValue() instanceof Float)) {
				gen.writeNumber(obj.getValue().doubleValue());
			} else if((obj.getValue() instanceof Long) || (obj.getValue() instanceof Integer)) {
				gen.writeNumber(obj.getValue().longValue());
			}
		}
		if((obj.getValues() != null) && (obj.getValues().size() > 0)) {
			if((obj.getValues().get(0) instanceof Float) || (obj.getValues().get(0) instanceof Double)) {
				double[] array = obj.getValues().stream().mapToDouble(n -> n.doubleValue()).toArray();
				gen.writeArray(array, 0, array.length);
			} else if((obj.getValues().get(0) instanceof Long) || (obj.getValues().get(0) instanceof Integer)) {
					long[] array = obj.getValues().stream().mapToLong(n -> n.longValue()).toArray();
					gen.writeArray(array, 0, array.length);
				} 
		}
	}
	
}
