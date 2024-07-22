package it.smartcommunitylabdhub.framework.k8s.infrastructure.monitor;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.kubernetes.client.custom.IntOrString;
import it.smartcommunitylabdhub.commons.events.RunnableChangedEvent;
import it.smartcommunitylabdhub.commons.events.RunnableMonitorObject;
import it.smartcommunitylabdhub.commons.exceptions.StoreException;
import it.smartcommunitylabdhub.commons.jackson.JacksonMapper;
import it.smartcommunitylabdhub.commons.services.RunnableStore;
import it.smartcommunitylabdhub.framework.k8s.jackson.IntOrStringMixin;
import it.smartcommunitylabdhub.framework.k8s.runnables.K8sRunnable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.util.Assert;

@Slf4j
public abstract class K8sBaseMonitor<T extends K8sRunnable> implements Runnable {

    //custom object mapper with mixIn for IntOrString
    protected static final ObjectMapper mapper = JacksonMapper.CUSTOM_OBJECT_MAPPER.addMixIn(
        IntOrString.class,
        IntOrStringMixin.class
    );
    protected static final TypeReference<HashMap<String, Serializable>> typeRef = new TypeReference<
        HashMap<String, Serializable>
    >() {};
    protected static final TypeReference<ArrayList<HashMap<String, Serializable>>> arrayRef = new TypeReference<
        ArrayList<HashMap<String, Serializable>>
    >() {};

    protected final RunnableStore<T> store;
    protected ApplicationEventPublisher eventPublisher;

    protected Boolean collectLogs = Boolean.TRUE;
    protected Boolean collectMetrics = Boolean.TRUE;
    protected String collectResults = "default";

    protected K8sBaseMonitor(RunnableStore<T> runnableStore) {
        Assert.notNull(runnableStore, "runnable store is required");

        this.store = runnableStore;
    }

    @Autowired
    public void setEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Autowired
    public void setCollectLogs(@Value("${kubernetes.logs}") Boolean collectLogs) {
        this.collectLogs = collectLogs;
    }

    @Autowired
    public void setCollectMetrics(@Value("${kubernetes.metrics}") Boolean collectMetrics) {
        this.collectMetrics = collectMetrics;
    }

    @Autowired
    public void setCollectResults(@Value("${kubernetes.results}") String collectResults) {
        this.collectResults = collectResults;
    }

    @Override
    public void run() {
        monitor();
    }

    public void monitor() {
        log.debug("monitor all RUNNING...");
        store
            .findAll()
            .stream()
            .filter(runnable -> runnable.getState() != null && runnable.getState().equals("RUNNING"))
            .flatMap(runnable -> {
                log.debug("monitor run {}", runnable.getId());

                if (log.isTraceEnabled()) {
                    log.trace("runnable: {}", runnable);
                }
                return Stream.of(refresh(runnable));
            })
            .forEach(runnable -> {
                if (log.isTraceEnabled()) {
                    log.trace("refreshed: {}", runnable);
                }

                // Update the runnable
                try {
                    log.debug("store run {}", runnable.getId());
                    store.store(runnable.getId(), runnable);

                    publish(runnable);
                } catch (StoreException e) {
                    log.error("Error with runnable store: {}", e.getMessage());
                }
            });

        log.debug("monitor completed.");
    }

    public abstract T refresh(T runnable);

    protected void publish(T runnable) {
        if (eventPublisher != null) {
            log.debug("publish run {}", runnable.getId());

            // Send message to Serve manager
            eventPublisher.publishEvent(
                RunnableChangedEvent
                    .builder()
                    .runnable(runnable)
                    .runMonitorObject(
                        RunnableMonitorObject
                            .builder()
                            .runId(runnable.getId())
                            .stateId(runnable.getState())
                            .project(runnable.getProject())
                            .framework(runnable.getFramework())
                            .task(runnable.getTask())
                            .build()
                    )
                    .build()
            );
        }
    }
}
