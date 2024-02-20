package it.smartcommunitylabdhub.core.services;

import it.smartcommunitylabdhub.commons.infrastructure.base.BaseRunnable;
import it.smartcommunitylabdhub.commons.jackson.JacksonMapper;
import it.smartcommunitylabdhub.commons.services.RunnableStoreService;
import it.smartcommunitylabdhub.core.models.entities.runnable.RunnableEntity;
import it.smartcommunitylabdhub.core.repositories.RunnableRepository;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//TODO FIX generic typing
@Service // remove service...
public class RunnableStoreServiceImpl<T extends it.smartcommunitylabdhub.commons.infrastructure.Runnable>
        implements RunnableStoreService<T> {

    @Autowired
    private RunnableRepository runnableRepository;

    public T find(String id) {
        RunnableEntity entity = runnableRepository.findById(id);
        if (entity != null) {
            try {
                return JacksonMapper.CBOR_OBJECT_MAPPER.readValue(entity.getData(), getRunnableClass());
            } catch (IOException e) {
                // Handle deserialization error
            }
        }
        return null;
    }

    public void store(String id, T e) {
        try {
            byte[] data = JacksonMapper.CBOR_OBJECT_MAPPER.writeValueAsBytes(e);

            RunnableEntity entity = RunnableEntity.builder().id(id).data(data).build();

            runnableRepository.save(entity);
        } catch (IOException ex) {
            // Handle serialization error
        }
    }

    public List<T> findAll() {
        List<RunnableEntity> entities = runnableRepository.findAll();
        return entities
                .stream()
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

    public void delete(String id) {
        runnableRepository.delete(id);
    }

    //TODO FIX! take the class as parameter
    @SuppressWarnings("unchecked")
    private Class<T> getRunnableClass() {
        return (Class<T>) BaseRunnable.class;
    }
}
