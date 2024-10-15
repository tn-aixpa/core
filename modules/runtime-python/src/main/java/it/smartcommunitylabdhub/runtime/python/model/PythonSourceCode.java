package it.smartcommunitylabdhub.runtime.python.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import it.smartcommunitylabdhub.commons.models.objects.SourceCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PythonSourceCode
    extends SourceCode<it.smartcommunitylabdhub.runtime.python.model.PythonSourceCode.PythonSourceCodeLanguages> {

    @Nullable
    @Schema(
        title = "fields.sourceCode.init_function.title",
        description = "fields.sourceCode.init_function.description"
    )
    @JsonProperty("init_function")
    private String initFunction;

    @Override
    public PythonSourceCodeLanguages getLang() {
        return PythonSourceCodeLanguages.python;
    }

    public enum PythonSourceCodeLanguages {
        python,
    }
}
