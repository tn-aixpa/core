package it.smartcommunitylabdhub.core.dataitems.specs;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonValue;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.Assert;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TableSchema implements Serializable {

    private List<Field> fields;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Field implements Serializable {

        private String name;
        private String title;
        private FieldType type;
        private String format;
        private String example;
        private String description;

        private Map<String, Serializable> constraints;
    }

    public enum FieldType {
        STRING("string"),
        NUMBER("number"),
        INTEGER("integer"),
        BOOLEAN("boolean"),
        OBJECT("object"),
        ARRAY("array"),
        DATE("date"),
        TIME("time"),
        DATETIME("datetime"),
        YEAR("year"),
        YEARMONTH("yearmonth"),
        DURATION("duration"),
        GEOPOINT("geopoint"),
        GEOJSON("geojson"),
        ANY("any");

        private final String value;

        FieldType(String value) {
            Assert.hasText(value, "value cannot be empty");
            this.value = value;
        }

        @JsonValue
        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return value;
        }
    }
}
