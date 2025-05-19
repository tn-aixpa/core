package it.smartcommunitylabdhub.authorization.services;

import it.smartcommunitylabdhub.authorization.repositories.RefreshTokenRepository;
import it.smartcommunitylabdhub.commons.exceptions.StoreException;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JwtTokenScheduler {

    public static final int DEFAULT_DELAY = 24 * 60 * 60 * 1000; //every day
    public static final int INITIAL_DELAY = 6 * 1000; //wait 60s for start

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Scheduled(fixedDelay = DEFAULT_DELAY, initialDelay = INITIAL_DELAY)
    @Transactional
    public void deleteExpiredRefreshTokens() {
        try {
            Date now = Date.from(Instant.now());

            log.info("remove refresh tokens expired before {}", now);
            refreshTokenRepository.removeByExpirationTime(now);
        } catch (StoreException e) {
            log.error("error removing expired refresh tokens", e);
        }
    }
}
