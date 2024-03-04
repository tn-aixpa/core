package it.smartcommunitylabdhub.core.services;

import it.smartcommunitylabdhub.commons.exceptions.StoreException;
import it.smartcommunitylabdhub.commons.jackson.JacksonMapper;
import it.smartcommunitylabdhub.commons.services.RunnableStore;
import it.smartcommunitylabdhub.core.models.entities.runnable.RunnableEntity;
import it.smartcommunitylabdhub.core.repositories.RunnableRepository;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RunnableStoreServiceImpl<T extends it.smartcommunitylabdhub.commons.infrastructure.Runnable>
        implements RunnableStore<T> {

    private final Class<T> clazz;
    private final RunnableRepository runnableRepository;

    public RunnableStoreServiceImpl(Class<T> clazz, RunnableRepository runnableRepository) {
        this.clazz = clazz;
        this.runnableRepository = runnableRepository;
    }

    @Override
    public T find(String id) throws StoreException {
        log.debug("find runnable {} with id {}", clazz.getName(), id);

        RunnableEntity runnableEntity = runnableRepository.findById(id);
        if (runnableEntity == null) {
            return null;
        }

        try {
            return JacksonMapper.CBOR_OBJECT_MAPPER.readValue(runnableEntity.getData(), clazz);
        } catch (IOException ex) {
            // Handle serialization error
            log.error("error deserializing runnable: {}", ex.getMessage());
            throw new StoreException("error deserializing runnable");
        }
    }

    @Override
    public List<T> findAll() {
        log.debug("find all runnable {}", clazz.getName());

        List<RunnableEntity> entities = runnableRepository.findAll();
        return entities
                .stream()
                .map(entity -> {
                    try {
                        return JacksonMapper.CBOR_OBJECT_MAPPER.readValue(entity.getData(), clazz);
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
            byte[] data = JacksonMapper.CBOR_OBJECT_MAPPER.writeValueAsBytes(e);
            RunnableEntity entity = RunnableEntity.builder().id(id).data(data).build();

            Optional.ofNullable(find(id)).ifPresentOrElse(
                    (r) -> runnableRepository.update(entity),
                    () -> runnableRepository.save(entity));
        } catch (IOException ex) {
            // Handle serialization error
            log.error("error deserializing runnable: {}", ex.getMessage());
            throw new StoreException("error deserializing runnable");
        }
    }

    @Override
    public void remove(String id) throws StoreException {
        log.debug("remove runnable {} with id {}", clazz.getName(), id);

        runnableRepository.delete(id);
    }
}
