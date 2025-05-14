package it.smartcommunitylabdhub.core.triggers.lifecycle;

import it.smartcommunitylabdhub.commons.Fields;
import it.smartcommunitylabdhub.commons.accessors.fields.StatusFieldAccessor;
import it.smartcommunitylabdhub.commons.accessors.spec.RunSpecAccessor;
import it.smartcommunitylabdhub.commons.exceptions.DuplicatedEntityException;
import it.smartcommunitylabdhub.commons.exceptions.NoSuchEntityException;
import it.smartcommunitylabdhub.commons.exceptions.SystemException;
import it.smartcommunitylabdhub.commons.infrastructure.Actuator;
import it.smartcommunitylabdhub.commons.infrastructure.TriggerRun;
import it.smartcommunitylabdhub.commons.models.enums.RelationshipName;
import it.smartcommunitylabdhub.commons.models.enums.State;
import it.smartcommunitylabdhub.commons.models.metadata.RelationshipsMetadata;
import it.smartcommunitylabdhub.commons.models.relationships.RelationshipDetail;
import it.smartcommunitylabdhub.commons.models.run.Run;
import it.smartcommunitylabdhub.commons.models.run.RunBaseStatus;
import it.smartcommunitylabdhub.commons.models.task.Task;
import it.smartcommunitylabdhub.commons.models.trigger.Trigger;
import it.smartcommunitylabdhub.commons.models.trigger.TriggerBaseSpec;
import it.smartcommunitylabdhub.commons.models.trigger.TriggerBaseStatus;
import it.smartcommunitylabdhub.commons.models.trigger.TriggerEvent;
import it.smartcommunitylabdhub.commons.models.trigger.TriggerJob;
import it.smartcommunitylabdhub.commons.models.trigger.TriggerRunBaseStatus;
import it.smartcommunitylabdhub.commons.services.RunService;
import it.smartcommunitylabdhub.commons.services.TaskService;
import it.smartcommunitylabdhub.commons.utils.MapUtils;
import it.smartcommunitylabdhub.core.components.infrastructure.runtimes.ActuatorFactory;
import it.smartcommunitylabdhub.core.components.run.LifecycleManager;
import it.smartcommunitylabdhub.core.events.EntityAction;
import it.smartcommunitylabdhub.core.events.EntityOperation;
import it.smartcommunitylabdhub.core.runs.lifecycle.RunLifecycleManager;
import it.smartcommunitylabdhub.core.triggers.persistence.TriggerEntity;
import it.smartcommunitylabdhub.fsm.Fsm;
import it.smartcommunitylabdhub.fsm.exceptions.InvalidTransitionException;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;

@Slf4j
@Component
public class TriggerLifecycleManager extends LifecycleManager<Trigger, TriggerEntity> {

    @Autowired
    private TriggerFsmFactory fsmFactory;

    @Autowired
    private ActuatorFactory actuatorFactory;

    @Autowired
    private TaskService taskService;

    @Autowired
    private RunService runService;

    @Autowired
    private RunLifecycleManager runManager;

    /*
     * Actions: ask to perform
     */

    public Trigger run(@NotNull Trigger trigger) {
        log.debug("run trigger with id {}", trigger.getId());
        if (log.isTraceEnabled()) {
            log.trace("trigger: {}", trigger);
        }

        //resolve actuator
        Actuator<? extends TriggerBaseSpec, ? extends TriggerBaseStatus, ? extends TriggerRunBaseStatus> actuator =
            actuatorFactory.getActuator(trigger.getKind());
        if (actuator == null) {
            throw new IllegalArgumentException("invalid or unsupported actuator");
        }

        // build state machine on current context
        Fsm<State, TriggerEvent, TriggerContext, TriggerRun<? extends TriggerJob>> fsm = fsm(trigger, actuator);

        //execute update op with locking
        return exec(
            new EntityOperation<>(trigger, EntityAction.UPDATE),
            tr -> {
                try {
                    //perform via FSM
                    Optional<TriggerBaseStatus> status = fsm.perform(TriggerEvent.RUN, null);
                    State state = fsm.getCurrentState();

                    Map<String, Serializable> actuatorStatus = status.isPresent() ? status.get().toMap() : null;

                    //update status
                    TriggerBaseStatus baseStatus = TriggerBaseStatus.with(tr.getStatus());
                    baseStatus.setState(state.name());

                    tr.setStatus(MapUtils.mergeMultipleMaps(tr.getStatus(), actuatorStatus, baseStatus.toMap()));
                    return tr;
                } catch (InvalidTransitionException e) {
                    log.debug("Invalid transition {} -> {}", e.getFromState(), e.getToState());
                    return tr;
                }
            }
        );
    }

    public Trigger stop(@NotNull Trigger trigger) {
        log.debug("stop trigger with id {}", trigger.getId());
        if (log.isTraceEnabled()) {
            log.trace("trigger: {}", trigger);
        }

        //resolve actuator
        Actuator<? extends TriggerBaseSpec, ? extends TriggerBaseStatus, ? extends TriggerRunBaseStatus> actuator =
            actuatorFactory.getActuator(trigger.getKind());
        if (actuator == null) {
            throw new IllegalArgumentException("invalid or unsupported actuator");
        }

        // build state machine on current context
        Fsm<State, TriggerEvent, TriggerContext, TriggerRun<? extends TriggerJob>> fsm = fsm(trigger, actuator);

        //execute update op with locking
        return exec(
            new EntityOperation<>(trigger, EntityAction.UPDATE),
            tr -> {
                try {
                    //perform via FSM
                    Optional<TriggerBaseStatus> status = fsm.perform(TriggerEvent.STOP, null);
                    State state = fsm.getCurrentState();

                    Map<String, Serializable> actuatorStatus = status.isPresent() ? status.get().toMap() : null;

                    //update status
                    TriggerBaseStatus baseStatus = TriggerBaseStatus.with(tr.getStatus());
                    baseStatus.setState(state.name());

                    tr.setStatus(MapUtils.mergeMultipleMaps(tr.getStatus(), actuatorStatus, baseStatus.toMap()));
                    return tr;
                } catch (InvalidTransitionException e) {
                    log.debug("Invalid transition {} -> {}", e.getFromState(), e.getToState());
                    return tr;
                }
            }
        );
    }

    /*
     * Events: fire
     */
    public void onFire(@NotNull Trigger trigger, TriggerRun<? extends TriggerJob> run) {
        log.debug("fire trigger with id {}", run.getId());
        if (log.isTraceEnabled()) {
            log.trace("trigger: {}", run);
        }

        //resolve actuator
        Actuator<? extends TriggerBaseSpec, ? extends TriggerBaseStatus, ? extends TriggerRunBaseStatus> actuator =
            actuatorFactory.getActuator(trigger.getKind());
        if (actuator == null) {
            throw new IllegalArgumentException("invalid or unsupported actuator");
        }
        // build state machine on current context
        Fsm<State, TriggerEvent, TriggerContext, TriggerRun<? extends TriggerJob>> fsm = fsm(trigger, actuator);

        //execute read op with locking
        exec(
            new EntityOperation<>(trigger, EntityAction.UPDATE),
            tr -> {
                try {
                    //perform via FSM
                    Optional<TriggerRunBaseStatus> status = fsm.perform(TriggerEvent.FIRE, run);
                    TriggerRunBaseStatus trRunStatus = TriggerRunBaseStatus.builder().trigger(run).build();

                    Map<String, Serializable> actuatorRunStatus = status.isPresent() ? status.get().toMap() : null;
                    Map<String, Serializable> triggerRunStatus = MapUtils.mergeMultipleMaps(
                        actuatorRunStatus,
                        trRunStatus.toMap()
                    );
                    State state = fsm.getCurrentState();

                    log.debug("build run from template for trigger {}", tr.getId());

                    //access task details from ref, same as run
                    RunSpecAccessor specAccessor = RunSpecAccessor.with(tr.getSpec());
                    if (!StringUtils.hasText(specAccessor.getTaskId())) {
                        throw new IllegalArgumentException("spec: invalid task");
                    }

                    //fetch for build
                    TriggerBaseSpec baseSpec = TriggerBaseSpec.from(tr.getSpec());
                    Task task = taskService.getTask(specAccessor.getTaskId());

                    //build meta
                    RelationshipsMetadata relMetadata = RelationshipsMetadata
                        .builder()
                        .relationships(List.of(new RelationshipDetail(RelationshipName.PRODUCEDBY, null, tr.getKey())))
                        .build();

                    //status
                    RunBaseStatus baseStatus = RunBaseStatus.baseBuilder().state(State.CREATED.name()).build();
                    Map<String, Serializable> runStatus = MapUtils.mergeMultipleMaps(
                        triggerRunStatus,
                        baseStatus.toMap()
                    );

                    Map<String, Serializable> addSpec = Map.of(
                        Fields.FUNCTION,
                        baseSpec.getFunction(),
                        Fields.TASK,
                        baseSpec.getTask()
                    );

                    //build template
                    //TODO process with details
                    // Map<String, Serializable> details = run.getDetails();
                    Map<String, Serializable> template = baseSpec.getTemplate();

                    //build run from trigger template
                    Run taskRun = Run
                        .builder()
                        .kind(specAccessor.getRuntime() + "+run") //TODO hardcoded, to fix
                        .project(tr.getProject())
                        .user(run.getUser())
                        .spec(MapUtils.mergeMultipleMaps(template, addSpec))
                        .metadata(relMetadata.toMap())
                        .status(runStatus)
                        .build();

                    if (log.isTraceEnabled()) {
                        log.trace("built run: {}", taskRun);
                    }

                    //create run via service as CREATED
                    taskRun = runService.createRun(taskRun);

                    //build now
                    taskRun = runManager.build(taskRun);

                    //dispatch for run
                    //TODO evaluate detaching via async
                    taskRun = runManager.run(taskRun);

                    //update trigger status
                    //TODO evaluate storing trigger event
                    TriggerBaseStatus trStatus = TriggerBaseStatus.with(tr.getStatus());
                    trStatus.setState(state.name());

                    tr.setStatus(trStatus.toMap());
                    return tr;
                } catch (InvalidTransitionException e) {
                    log.debug("Invalid transition {} -> {}", e.getFromState(), e.getToState());
                    return tr;
                } catch (NoSuchEntityException | SystemException | DuplicatedEntityException | BindException e) {
                    log.error("Error creating run: {}", e.getMessage());
                    return tr;
                }
            }
        );
    }

    public void onError(@NotNull Trigger trigger, TriggerRun<? extends TriggerJob> run) {
        log.debug("error trigger with id {}", trigger.getId());
        if (log.isTraceEnabled()) {
            log.trace("trigger: {}", trigger);
        }

        //resolve actuator
        Actuator<? extends TriggerBaseSpec, ? extends TriggerBaseStatus, ? extends TriggerRunBaseStatus> actuator =
            actuatorFactory.getActuator(trigger.getKind());
        if (actuator == null) {
            throw new IllegalArgumentException("invalid or unsupported actuator");
        }

        // build state machine on current context
        Fsm<State, TriggerEvent, TriggerContext, TriggerRun<? extends TriggerJob>> fsm = fsm(trigger, actuator);

        //execute read op with locking
        exec(
            new EntityOperation<>(trigger, EntityAction.UPDATE),
            tr -> {
                try {
                    //perform via FSM
                    Optional<RunBaseStatus> status = fsm.goToState(State.ERROR, run);
                    State state = fsm.getCurrentState();

                    //update status
                    RunBaseStatus runBaseStatus = status.orElse(RunBaseStatus.with(tr.getStatus()));
                    runBaseStatus.setState(state.name());
                    return tr;
                } catch (InvalidTransitionException e) {
                    log.debug("Invalid transition {} -> {}", e.getFromState(), e.getToState());
                    return tr;
                }
            }
        );
        //TODO evaluate callback to actuator on error
    }

    private Fsm<State, TriggerEvent, TriggerContext, TriggerRun<? extends TriggerJob>> fsm(
        Trigger trigger,
        Actuator<?, ?, ?> actuator
    ) {
        //initial state is current state
        State initialState = State.valueOf(StatusFieldAccessor.with(trigger.getStatus()).getState());
        TriggerContext context = TriggerContext.builder().trigger(trigger).actuator(actuator).build();

        // create state machine via factory
        return fsmFactory.create(initialState, context);
    }
}
