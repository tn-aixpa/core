package it.smartcommunitylabdhub.core.models.converters.types;

import it.smartcommunitylabdhub.core.annotations.common.ConverterType;
import it.smartcommunitylabdhub.core.components.fsm.enums.RunState;
import it.smartcommunitylabdhub.core.exceptions.CustomException;
import it.smartcommunitylabdhub.core.models.converters.interfaces.Converter;
import it.smartcommunitylabdhub.core.models.entities.run.RunEntity;
import it.smartcommunitylabdhub.core.models.entities.run.Run;

@ConverterType(type = "run")
public class RunConverter implements Converter<Run, RunEntity> {

    @Override
    public RunEntity convert(Run runDTO) throws CustomException {
        return RunEntity.builder()
                .id(runDTO.getId())
                .project(runDTO.getProject())
                .kind(runDTO.getKind())
                .state(runDTO.getState() == null ? RunState.CREATED
                        : RunState.valueOf(runDTO.getState()))
                .build();
    }

    @Override
    public Run reverseConvert(RunEntity run) throws CustomException {
        return Run.builder()
                .id(run.getId())
                .project(run.getProject())
                .kind(run.getKind())
                .state(run.getState() == null ? RunState.CREATED.name() : run.getState().name())
                .created(run.getCreated())
                .updated(run.getUpdated())
                .build();
    }

}
