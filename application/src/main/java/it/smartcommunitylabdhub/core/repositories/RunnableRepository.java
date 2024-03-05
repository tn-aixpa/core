package it.smartcommunitylabdhub.core.repositories;

import it.smartcommunitylabdhub.core.models.entities.runnable.RunnableEntity;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RunnableRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void save(String clazz, RunnableEntity entity) {
        String sql = "INSERT INTO runnable (id, clazz, data) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, entity.getId(), clazz, entity.getData());
    }

    public void update(String clazz, RunnableEntity entity) {
        String sql = "UPDATE runnable SET data = ? WHERE id = ? and clazz = ?";
        jdbcTemplate.update(sql, entity.getData(), entity.getId(), clazz);
    }

    public RunnableEntity findById(String clazz, String id) {

        try {
            String sql = "SELECT * FROM runnable WHERE id = ? and clazz = ?";
            return jdbcTemplate.queryForObject(sql, BeanPropertyRowMapper.newInstance(RunnableEntity.class), id, clazz);
        } catch (Exception e) {
            return null;
        }

    }

    public List<RunnableEntity> findAll(String clazz) {
        try {
            String sql = "SELECT * FROM runnable WHERE clazz = ?";
            return jdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(RunnableEntity.class), clazz);
        } catch (Exception e) {
            return List.of();
        }
    }

    public void delete(String clazz, String id) {
        String sql = "DELETE FROM runnable WHERE id = ? AND clazz = ?";
        jdbcTemplate.update(sql, id, clazz);
    }
}
