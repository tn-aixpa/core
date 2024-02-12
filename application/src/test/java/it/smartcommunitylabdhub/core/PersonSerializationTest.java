package it.smartcommunitylabdhub.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PersonSerializationTest {

    @Test
    public void testPersonSerialization() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.setMixInAnnotation(Person.class, PersonMixin.class);
        objectMapper.registerModule(module);

        Person person = new Person();
        person.setName("John Doe");
        person.setAge(30);

        String json = objectMapper.writeValueAsString(person);

        // Verify the serialized JSON
        assertEquals("{\"full_name\":\"John Doe\",\"age\":\"60\"}", json);

        // Deserialize and verify
        Person deserializedPerson = objectMapper.readValue(json, Person.class);
        assertEquals(person.getName(), deserializedPerson.getName());
        assertEquals(person.getAge(), deserializedPerson.getAge());
    }

    public static class Person {
        private String name;
        private int age;

        // Getters and setters (or lombok annotations) omitted for brevity
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }

    public abstract static class PersonMixin {
        @JsonValue
        abstract String serializePerson();

        @JsonProperty("full_name")
        abstract String getName();

        @JsonSerialize(using = CustomAgeSerializer.class)
        abstract int getAge();
    }

    public static class CustomAgeSerializer extends StdSerializer<Integer> {
        public CustomAgeSerializer() {
            this(null);
        }

        public CustomAgeSerializer(Class<Integer> t) {
            super(t);
        }

        @Override
        public void serialize(Integer age, com.fasterxml.jackson.core.JsonGenerator jsonGenerator, com.fasterxml.jackson.databind.SerializerProvider serializerProvider) throws IOException {
            // Perform custom calculation or transformation on the age before serialization
            int modifiedAge = age * 2;

            // Serialize the modified age
            jsonGenerator.writeString(String.valueOf(modifiedAge));
        }
    }
}
