package it.smartcommunitylabdhub.core.repositories;

import it.smartcommunitylabdhub.core.models.entities.runnable.RunnableEntity;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.List;
import javax.sql.DataSource;
import org.joda.time.Instant;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

@Repository
public class RunnableRepository {

    private static final String INSERT_SQL =
        "INSERT INTO runnables (id, created, updated, _clazz, _data) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL =
        "UPDATE runnables SET data = ? and updated = ? WHERE id = ? and _clazz = ?";
    private static final String SELECT_SQL = "SELECT * FROM runnables WHERE id = ? and _clazz = ?";
    private static final String SELECT_ALL_SQL = "SELECT * FROM runnables WHERE _clazz = ?";
    private static final String DELETE_SQL = "DELETE FROM runnables WHERE id = ? AND _clazz = ?";

    private final JdbcTemplate jdbcTemplate;
    private RowMapper<RunnableEntity> rowMapper;

    public RunnableRepository(DataSource dataSource) {
        Assert.notNull(dataSource, "DataSource required");
        this.jdbcTemplate = new JdbcTemplate(dataSource);

        this.rowMapper = new RunnableEntityRowMapper();
    }

    public void save(String clazz, RunnableEntity entity) {
        if (clazz == null || entity == null || entity.getId() == null) {
            throw new IllegalArgumentException("invalid data");
        }

        Timestamp now = new Timestamp(Instant.now().toDate().getTime());

        jdbcTemplate.update(
            INSERT_SQL,
            new Object[] { entity.getId(), now, now, clazz, entity.getData() },
            new int[] { Types.VARCHAR, Types.TIMESTAMP, Types.TIMESTAMP, Types.VARCHAR, Types.BLOB }
        );
    }

    public void update(String clazz, String id, RunnableEntity entity) {
        if (clazz == null || id == null || entity == null) {
            throw new IllegalArgumentException("invalid data");
        }

        Timestamp now = new Timestamp(Instant.now().toDate().getTime());

        jdbcTemplate.update(
            UPDATE_SQL,
            new Object[] { entity.getData(), now, id, clazz },
            new int[] { Types.BLOB, Types.TIMESTAMP, Types.VARCHAR, Types.VARCHAR }
        );
    }

    public RunnableEntity find(String clazz, String id) {
        if (clazz == null || id == null) {
            throw new IllegalArgumentException("invalid data");
        }
        try {
            return jdbcTemplate.queryForObject(SELECT_SQL, rowMapper, id, clazz);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<RunnableEntity> findAll(String clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("invalid data");
        }

        return jdbcTemplate.query(SELECT_ALL_SQL, rowMapper, clazz);
    }

    public void delete(String clazz, String id) {
        if (clazz == null || id == null) {
            throw new IllegalArgumentException("invalid data");
        }

        jdbcTemplate.update(DELETE_SQL, id, clazz);
    }

    private class RunnableEntityRowMapper implements RowMapper<RunnableEntity> {

        @Override
        public RunnableEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
            String id = rs.getString("id");
            Timestamp created = rs.getTimestamp("created");
            Timestamp updated = rs.getTimestamp("created");

            String clazz = rs.getString("_clazz");
            byte[] data = rs.getBytes("_data");

            if (id == null || clazz == null) {
                return null;
            }

            return new RunnableEntity(id, created, updated, clazz, data);
        }
    }
}
