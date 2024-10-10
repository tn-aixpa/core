package it.smartcommunitylabdhub.authorization.repositories;

import it.smartcommunitylabdhub.authorization.model.RefreshTokenEntity;
import java.util.Date;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

public interface RefreshTokenRepository
    extends JpaRepository<RefreshTokenEntity, String>, JpaSpecificationExecutor<RefreshTokenEntity> {
    // Select the token with a FOR UPDATE lock
    @Query(value = "SELECT * FROM refresh_tokens WHERE id = :id FOR UPDATE", nativeQuery = true)
    Optional<RefreshTokenEntity> findByIdForUpdate(@Param("id") @NonNull String id);

    // Delete the token
    @Modifying
    @Query(value = "DELETE FROM RefreshTokenEntity WHERE id = :tokenId")
    void deleteById(@Param("tokenId") @NonNull String tokenId);

    @Modifying
    @Query(value = "DELETE FROM RefreshTokenEntity WHERE expirationTime < :expirationTime")
    void deleteByExpirationTime(@Param("expirationTime") @NonNull Date expirationTime);
}
