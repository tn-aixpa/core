package it.smartcommunitylabdhub.authorization.repositories;

import it.smartcommunitylabdhub.authorization.model.RefreshTokenEntity;
import jakarta.transaction.Transactional;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RefreshTokenRepository
    extends JpaRepository<RefreshTokenEntity, String>, JpaSpecificationExecutor<RefreshTokenEntity> {
    // Select the token with a FOR UPDATE lock
    @Query(value = "SELECT * FROM RefreshTokenEntity WHERE id = :tokenId FOR UPDATE", nativeQuery = true)
    Optional<RefreshTokenEntity> findByIdForUpdate(@Param("tokenId") String tokenId);

    // Delete the token
    @Modifying
    @Query(value = "DELETE FROM RefreshTokenEntity WHERE id = :tokenId", nativeQuery = true)
    void deleteById(@Param("tokenId") String tokenId);
}
