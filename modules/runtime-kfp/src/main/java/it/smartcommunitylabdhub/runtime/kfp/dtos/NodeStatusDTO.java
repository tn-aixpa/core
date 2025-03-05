package it.smartcommunitylabdhub.runtime.kfp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class NodeStatusDTO implements Serializable {
    private String id;
    private String name;
    @JsonProperty("display_name")
    private String displayName;
    private String type;
    private List<String> children;
    private String state;
    @JsonProperty("start_time")
    private OffsetDateTime startTime;
    @JsonProperty("end_time")
    private OffsetDateTime endTime;
    @JsonProperty("exit_code")
    private String exitCode;
    private List<Map<String, String>> inputs;
    private List<Map<String, String>> outputs;
    private String function;
    @JsonProperty("function_id")
    private String functionId;
    @JsonProperty("run_id")
    private String runId;
    private String action;
}
