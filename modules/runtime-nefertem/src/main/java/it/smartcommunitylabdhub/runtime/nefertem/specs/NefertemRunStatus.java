package it.smartcommunitylabdhub.runtime.nefertem.specs;

import com.fasterxml.jackson.annotation.JsonInclude;

import it.smartcommunitylabdhub.commons.models.run.RunBaseStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NefertemRunStatus extends RunBaseStatus {}
