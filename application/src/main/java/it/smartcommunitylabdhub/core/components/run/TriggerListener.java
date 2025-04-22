package it.smartcommunitylabdhub.core.components.run;

import it.smartcommunitylabdhub.commons.infrastructure.RunRunnable;
import it.smartcommunitylabdhub.commons.infrastructure.TriggerRun;
import it.smartcommunitylabdhub.commons.models.run.Run;
import it.smartcommunitylabdhub.commons.models.trigger.Trigger;
import it.smartcommunitylabdhub.commons.models.trigger.TriggerExecutionEvent;
import it.smartcommunitylabdhub.commons.models.trigger.TriggerJob;
import it.smartcommunitylabdhub.commons.services.TriggerService;
import java.util.Collections;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.BiConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.concurrent.DelegatingSecurityContextRunnable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.TransientSecurityContext;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@Slf4j
public class TriggerListener {

    private final TriggerService triggerService;
    private final TriggerLifecycleManager triggerManager;
    private final ThreadPoolTaskExecutor executor;

    public TriggerListener(TriggerService triggerService, TriggerLifecycleManager triggerManager) {
        Assert.notNull(triggerManager, "trigger manager is required");
        Assert.notNull(triggerService, "trigger service is required");
        this.triggerService = triggerService;
        this.triggerManager = triggerManager;

        //create local executor pool to delegate+inject user context in async tasks
        executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setThreadNamePrefix("triggerlm-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
    }

    /*
     * Delegating security context
     */
    private void wrap(Trigger trigger, TriggerRun<TriggerJob> run, BiConsumer<Trigger, TriggerRun<TriggerJob>> lambda) {
        String user = run.getUser();

        log.trace("wrap trigger callback for user {}", String.valueOf(user));
        if (user != null) {
            //wrap in a security context
            //TODO restore user roles/context?
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                user,
                null,
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"))
            );
            Runnable wrapped = DelegatingSecurityContextRunnable.create(
                () -> lambda.accept(trigger, run),
                new TransientSecurityContext(auth)
            );
            executor.execute(wrapped);
        } else {
            //run as system
            executor.execute(() -> lambda.accept(trigger, run));
        }
    }

    @Async
    @EventListener
    public void receive(TriggerExecutionEvent<TriggerJob> event) {
        if (event.getEvent() == null) {
            return;
        }

        log.debug("receive event {} for {}", event.getEvent(), event.getId());
        if (log.isTraceEnabled()) {
            log.trace("event: {}", event);
        }

        String id = event.getId();

        //load trigger from db
        Trigger trigger = triggerService.findTrigger(event.getId());
        if (trigger == null) {
            log.error("Trigger with id {} not found", id);
            return;
        }

        switch (event.getEvent()) {
            case FIRE:
                // triggerManager.fire(trigger);
                wrap(trigger, event.getRun(), triggerManager::onFire);
                break;
            // case RUN:
            //     triggerManager.run(trigger);
            //     break;
            // case STOP:
            //     break;
            case ERROR:
                wrap(trigger, event.getRun(), triggerManager::onError);
                break;
            default:
                log.debug("Event {} for trigger id {} not managed", event.getEvent(), id);
                break;
        }
    }
}
