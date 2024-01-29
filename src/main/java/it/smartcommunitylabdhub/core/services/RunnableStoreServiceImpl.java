package it.smartcommunitylabdhub.core.services;

import it.smartcommunitylabdhub.core.components.infrastructure.factories.runnables.BaseRunnable;
import it.smartcommunitylabdhub.core.models.entities.runnable.RunnableEntity;
import it.smartcommunitylabdhub.core.repositories.RunnableRepository;
import it.smartcommunitylabdhub.core.services.interfaces.RunnableStoreService;
import it.smartcommunitylabdhub.core.utils.jackson.JacksonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class RunnableStoreServiceImpl<T> implements RunnableStoreService<T> {

    @Autowired
    private RunnableRepository runnableRepository;

    @Override
    public T find(String id) {
        RunnableEntity entity = runnableRepository.findById(id).orElse(null);
        if (entity != null) {
            try {
                return JacksonMapper.CBOR_OBJECT_MAPPER.readValue(entity.getData(), getRunnableClass());
            } catch (IOException e) {
                // Handle deserialization error
            }
        }
        return null;
    }

    @Override
    public void store(String id, T e) {

        try {
            byte[] data = JacksonMapper.CBOR_OBJECT_MAPPER.writeValueAsBytes(e);

            RunnableEntity entity = RunnableEntity.builder()
                    .id(id)
                    .data(data).build();

            runnableRepository.save(entity);
        } catch (IOException ex) {
            // Handle serialization error
        }
    }

    @Override
    public List<T> findAll() {
        return runnableRepository.findAll().stream()
                .map(entity -> {
                    try {
                        return JacksonMapper.CBOR_OBJECT_MAPPER.readValue(entity.getData(), getRunnableClass());
                    } catch (IOException e) {
                        // Handle deserialization error
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }


    @SuppressWarnings("unchecked")
    private Class<T> getRunnableClass() {
        return (Class<T>) BaseRunnable.class;
    }
}
