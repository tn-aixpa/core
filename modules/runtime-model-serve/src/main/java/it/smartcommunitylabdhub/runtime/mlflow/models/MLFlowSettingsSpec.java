package it.smartcommunitylabdhub.runtime.mlflow.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class MLFlowSettingsSpec {

    private String name;
    private String implementation;
    private MLFlowSettingsParameters parameters;
    private String platform;
}
