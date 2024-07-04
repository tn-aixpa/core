package it.smartcommunitylabdhub.core.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.smartcommunitylabdhub.commons.exceptions.StoreException;
import it.smartcommunitylabdhub.commons.infrastructure.RunRunnable;
import it.smartcommunitylabdhub.commons.jackson.JacksonMapper;
import it.smartcommunitylabdhub.commons.services.RunnableStore;
import it.smartcommunitylabdhub.core.models.entities.RunnableEntity;
import it.smartcommunitylabdhub.core.repositories.RunnableRepository;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ResolvableType;
import org.springframework.util.Assert;

@Slf4j
public class RunnableStoreImpl<T extends RunRunnable> implements RunnableStore<T> {

    private final Class<T> clazz;
    private final RunnableRepository runnableRepository;
    private ObjectMapper objectMapper;

    public RunnableStoreImpl(Class<T> clazz, RunnableRepository runnableRepository) {
        this.clazz = clazz;
        this.runnableRepository = runnableRepository;

        //use CBOR mapper as default
        this.objectMapper = JacksonMapper.CBOR_OBJECT_MAPPER;
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        Assert.notNull(objectMapper, "object mapper can not be null");
        this.objectMapper = objectMapper;
    }

    @Override
    public T find(String id) throws StoreException {
        log.debug("find runnable {} with id {}", clazz.getName(), id);

        RunnableEntity runnableEntity = runnableRepository.find(clazz.getName(), id);
        if (runnableEntity == null) {
            return null;
        }

        try {
            return objectMapper.readValue(runnableEntity.getData(), clazz);
        } catch (IOException ex) {
            // Handle serialization error
            log.error("error deserializing runnable: {}", ex.getMessage());
            throw new StoreException("error deserializing runnable");
        }
    }

    @Override
    public List<T> findAll() {
        log.debug("find all runnable {}", clazz.getName());

        List<RunnableEntity> entities = runnableRepository.findAll(clazz.getName());
        return entities
            .stream()
            .map(entity -> {
                try {
                    return objectMapper.readValue(entity.getData(), clazz);
                } catch (IOException e) {
                    // Handle deserialization error
                    log.error("error deserializing runnable: {}", e.getMessage());
                    return null;
                }
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    @Override
    public void store(String id, T e) throws StoreException {
        log.debug("store runnable {} with id {}", clazz.getName(), id);
        try {
            byte[] data = objectMapper.writeValueAsBytes(e);
            RunnableEntity entity = RunnableEntity.builder().id(id).data(data).build();

            Optional
                .ofNullable(find(id))
                .ifPresentOrElse(
                    r -> runnableRepository.update(clazz.getName(), r.getId(), entity),
                    () -> runnableRepository.save(clazz.getName(), entity)
                );
        } catch (IOException ex) {
            // Handle serialization error
            log.error("error deserializing runnable: {}", ex.getMessage());
            throw new StoreException("error deserializing runnable");
        }
    }

    @Override
    public void remove(String id) throws StoreException {
        log.debug("remove runnable {} with id {}", clazz.getName(), id);

        runnableRepository.delete(clazz.getName(), id);
    }

    @Override
    public ResolvableType getResolvableType() {
        return ResolvableType.forClass(this.clazz);
    }
}
