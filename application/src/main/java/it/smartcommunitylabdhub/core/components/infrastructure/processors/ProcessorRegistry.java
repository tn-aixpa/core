package it.smartcommunitylabdhub.core.components.infrastructure.processors;

import it.smartcommunitylabdhub.commons.annotations.common.RunProcessorType;
import it.smartcommunitylabdhub.commons.infrastructure.RunProcessor;
import it.smartcommunitylabdhub.commons.models.entities.run.RunBaseStatus;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class ProcessorRegistry implements ApplicationListener<ContextRefreshedEvent> {

    private final Map<String, List<Map.Entry<String, RunProcessor<? extends RunBaseStatus>>>> registry =
        new ConcurrentHashMap<>();

    private final ApplicationContext applicationContext;

    @Autowired
    public ProcessorRegistry(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void onApplicationEvent(@NotNull ContextRefreshedEvent event) {
        applicationContext
            .getBeansWithAnnotation(RunProcessorType.class)
            .entrySet()
            .forEach(e -> {
                String name = e.getKey();
                Object bean = e.getValue();
                RunProcessorType annotation = bean.getClass().getAnnotation(RunProcessorType.class);

                if (bean instanceof RunProcessor && (annotation != null)) {
                    for (String stage : annotation.stages()) {
                        //register if missing
                        List<Entry<String, RunProcessor<?>>> processors = registry.computeIfAbsent(
                            stage,
                            k -> new ArrayList<>()
                        );

                        if (processors.stream().noneMatch(p -> name.equals(p.getKey()))) {
                            processors.add(Map.entry(name, (RunProcessor<?>) bean));
                        }
                    }
                }
            });
    }

    public List<RunProcessor<? extends RunBaseStatus>> getProcessors(String stage) {
        return registry
            .getOrDefault(stage, Collections.emptyList())
            .stream()
            .map(Entry::getValue)
            .collect(Collectors.toList());
    }
}
