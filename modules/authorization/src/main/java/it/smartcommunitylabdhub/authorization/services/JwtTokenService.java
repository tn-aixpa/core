package it.smartcommunitylabdhub.authorization.services;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jose.crypto.ECDSAVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import it.smartcommunitylabdhub.authorization.components.JWKSetKeyStore;
import it.smartcommunitylabdhub.authorization.exceptions.JwtTokenServiceException;
import it.smartcommunitylabdhub.authorization.model.RefreshTokenEntity;
import it.smartcommunitylabdhub.authorization.model.UserAuthentication;
import it.smartcommunitylabdhub.authorization.repositories.RefreshTokenRepository;
import it.smartcommunitylabdhub.authorization.utils.SecureKeyGenerator;
import it.smartcommunitylabdhub.commons.config.ApplicationProperties;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import java.io.IOException;
import java.sql.SQLTimeoutException;
import java.time.Instant;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.serializer.support.SerializationDelegate;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.keygen.StringKeyGenerator;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.jwt.JwtClaimValidator;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

@Service
@Slf4j
//TODO extract an interface
public class JwtTokenService implements InitializingBean {

    private static final int DEFAULT_ACCESS_TOKEN_DURATION = 3600 * 8; //8 hours
    private static final int DEFAULT_REFRESH_TOKEN_DURATION = 3600 * 24 * 30; //30 days
    private static final int DEFAULT_KEY_LENGTH = 54;

    private static final String CLAIM_AUTHORITIES = "authorities";

    @Autowired
    //TODO move to JDBC!
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private JWKSetKeyStore keyStore;

    @Value("${jwt.client-id}")
    private String clientId;

    private String audience;
    private String issuer;

    private int accessTokenDuration = DEFAULT_ACCESS_TOKEN_DURATION;
    private int refreshTokenDuration = DEFAULT_REFRESH_TOKEN_DURATION;

    //we need to keep the key along with singer/verifier
    private JWK jwk;
    private JWSSigner signer;
    private JWSVerifier verifier;

    private JwtDecoder decoder;

    private JwtAuthenticationConverter authenticationConverter;

    //keygen
    private StringKeyGenerator keyGenerator;

    //custom serialization
    SerializationDelegate serializer = new SerializationDelegate(this.getClass().getClassLoader());

    public JwtTokenService() {
        log.debug("create jwks service");
        this.keyGenerator = new SecureKeyGenerator(DEFAULT_KEY_LENGTH);
    }

    @Autowired
    public void setAccessTokenDuration(@Value("${jwt.access-token.duration}") Integer accessTokenDuration) {
        if (accessTokenDuration != null) {
            this.accessTokenDuration = accessTokenDuration.intValue();
        }
    }

    @Autowired
    public void setRefreshTokenDuration(@Value("${jwt.refresh-token.duration}") Integer refreshTokenDuration) {
        if (refreshTokenDuration != null) {
            this.refreshTokenDuration = refreshTokenDuration.intValue();
        }
    }

    @Autowired
    public void setApplicationProperties(ApplicationProperties applicationProperties) {
        Assert.notNull(applicationProperties, "app properties are required");
        this.issuer = applicationProperties.getEndpoint();
        this.audience = applicationProperties.getName();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.hasText(audience, "audience can not be null");
        Assert.hasText(issuer, "issuer can not be null");

        if (keyStore != null) {
            //build signer for the given keys
            this.jwk = keyStore.getJwk();

            if (jwk != null) {
                try {
                    this.verifier = buildVerifier(jwk);
                    this.signer = buildSigner(jwk);
                    this.decoder = buildJwtDecoder(jwk);
                    this.authenticationConverter = buildAuthoritiesConverter();
                } catch (JOSEException e) {
                    log.warn("Exception loading signer/verifier", e);
                }
            }
        }
    }

    /*
     * Export config
     */

    public int getAccessTokenDuration() {
        return accessTokenDuration;
    }

    public int getRefreshTokenDuration() {
        return refreshTokenDuration;
    }

    public String getIssuer() {
        return issuer;
    }

    public String getAudience() {
        return audience;
    }

    public String getClientId() {
        return clientId;
    }

    public JWSVerifier getVerifier() {
        return verifier;
    }

    public JwtDecoder getDecoder() {
        return decoder;
    }

    public JwtAuthenticationConverter getAuthenticationConverter() {
        return authenticationConverter;
    }

    /*
     * Access tokens
     */
    public String generateAccessTokenAsString(@NotNull UserAuthentication<?> authentication)
        throws JwtTokenServiceException {
        // Serialize to compact form
        SignedJWT jwt = generateAccessToken(authentication);
        String jwtToken = jwt.serialize();

        if (log.isTraceEnabled()) {
            log.trace("Generated JWT token: {}", jwtToken);
        }

        return jwtToken;
    }

    public SignedJWT generateAccessToken(@NotNull UserAuthentication<?> authentication)
        throws JwtTokenServiceException {
        if (signer == null) {
            throw new UnsupportedOperationException("signer not available");
        }

        try {
            JWSAlgorithm jwsAlgorithm = JWSAlgorithm.parse(jwk.getAlgorithm().getName());

            Instant now = Instant.now();

            // build access token claims
            JWTClaimsSet.Builder claims = new JWTClaimsSet.Builder()
                .subject(authentication.getName())
                .issuer(issuer)
                .issueTime(Date.from(now))
                .audience(audience)
                .jwtID(keyGenerator.generateKey())
                .expirationTime(Date.from(now.plusSeconds(accessTokenDuration)));

            //define authorities as claims
            List<String> authorities = authentication
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

            claims.claim(CLAIM_AUTHORITIES, authorities);

            //add client if set
            if (StringUtils.hasText(clientId)) {
                claims.claim("client_id", clientId);
            }

            //include any credential available
            //NOTE: we expect claims to NOT clash
            if (authentication.getCredentials() != null) {
                authentication
                    .getCredentials()
                    .stream()
                    .filter(c -> c != null)
                    .map(c -> {
                        if (c instanceof CredentialsContainer) {
                            ((CredentialsContainer) c).eraseCredentials();
                        }
                        return c;
                    })
                    .flatMap(c -> c.toMap().entrySet().stream())
                    .forEach(c -> claims.claim(c.getKey(), c.getValue()));
            }

            // build and sign
            JWTClaimsSet claimsSet = claims.build();
            JWSHeader header = new JWSHeader.Builder(jwsAlgorithm).keyID(jwk.getKeyID()).build();
            SignedJWT jwt = new SignedJWT(header, claimsSet);
            jwt.sign(signer);

            return jwt;
        } catch (JOSEException e) {
            log.error("Error generating JWT token", e);
            return null;
        }
    }

    /*
     * Refresh tokens
     */
    public String generateRefreshToken(@NotNull UserAuthentication<?> authentication, @NotNull SignedJWT accessToken)
        throws JwtTokenServiceException {
        log.debug("generate refresh token for {}", authentication.getName());
        if (log.isTraceEnabled()) {
            log.trace("access token: {}", accessToken.serialize());
        }

        //refresh tokens are opaque
        //use UUID as secret value
        String jti = keyGenerator.generateKey();
        Instant now = Instant.now();

        // //derive a new access token with different expiration
        // JWTClaimsSet.Builder claims = new JWTClaimsSet.Builder(
        //     JWTClaimsSet.parse(accessToken.getPayload().toJSONObject())
        // );
        // claims.expirationTime(Date.from(now.plusSeconds(refreshTokenDuration)));
        // JWSAlgorithm jwsAlgorithm = JWSAlgorithm.parse(jwk.getAlgorithm().getName());
        // JWSHeader header = new JWSHeader.Builder(jwsAlgorithm).keyID(jwk.getKeyID()).build();
        // SignedJWT jwt = new SignedJWT(header, claims.build());
        // jwt.sign(signer);

        //store auth object serialized
        // byte[] auth = SerializationUtils.serialize(authentication);
        try {
            byte[] auth = serializer.serializeToByteArray(authentication);

            log.debug("store refresh token for {} with id {}", authentication.getName(), jti);

            // store Refresh Token into db
            RefreshTokenEntity refreshToken = RefreshTokenEntity
                .builder()
                .id(jti)
                .subject(authentication.getName())
                .authentication(auth)
                .issuedTime(Date.from(now))
                .expirationTime(Date.from(now.plusSeconds(refreshTokenDuration)))
                .build();

            //save
            refreshToken = refreshTokenRepository.saveAndFlush(refreshToken);

            //id is the token value
            return refreshToken.getId();
        } catch (IOException e) {
            throw new JwtTokenServiceException(e.getMessage());
        }
    }

    @Transactional(dontRollbackOn = { PessimisticLockingFailureException.class, SQLTimeoutException.class })
    public @NotNull UserAuthentication<?> consume(String refreshToken) {
        // try {
        if (verifier == null) {
            throw new UnsupportedOperationException("verifier not available");
        }

        log.debug("consume refresh token: {}", refreshToken);

        //value is the ID for the table
        String id = refreshToken;

        // Lock the token
        Optional<RefreshTokenEntity> tokenEntity = refreshTokenRepository.findByIdForUpdate(id);
        if (tokenEntity.isEmpty()) {
            log.debug("refresh token does not exists: {} id {}", refreshToken, id);
            throw new JwtTokenServiceException("Refresh token does not exist");
        }

        RefreshTokenEntity token = tokenEntity.get();

        if (log.isTraceEnabled()) {
            log.trace("token: {}", token);
        }

        // // Parse the access token
        // String accessToken = token.getToken();
        // if (!StringUtils.hasText(accessToken)) {
        //     //no access token stored along refresh, nothing to use to rebuild context
        //     throw new JwtTokenServiceException("Missing access token");
        // }

        // SignedJWT signedJWT = SignedJWT.parse(accessToken);

        // // Verify the token signature
        // if (!signedJWT.verify(verifier)) {
        //     throw new JwtTokenServiceException("Invalid access token");
        // }

        // Delete the token after usage: it matches the subject and should not be reused
        refreshTokenRepository.deleteById(token.getId());

        // Check expiration
        if (token.getExpirationTime().before(Date.from(Instant.now()))) {
            throw new JwtTokenServiceException("Refresh token has expired");
        }
        try {
            //deserialize authentication
            byte[] bytes = token.getAuthentication();
            if (bytes == null || bytes.length == 0) {
                throw new JwtTokenServiceException("Missing authentication for token");
            }

            // @SuppressWarnings("deprecation")
            // UserAuthentication<?> user = (UserAuthentication<?>) SerializationUtils.deserialize(bytes);

            UserAuthentication<?> user = (UserAuthentication<?>) serializer.deserializeFromByteArray(bytes);

            log.debug("Refresh token successfully consumed and removed from repository");
            return user;
            // } catch (ParseException e) {
            //     throw new JwtTokenServiceException("error parsing token", e);
            // } catch (JOSEException e) {
            //     throw new JwtTokenServiceException("Error verifying JWT token", e);
            // }

        } catch (IOException e) {
            throw new JwtTokenServiceException(e.getMessage());
        }
    }

    private JWSSigner buildSigner(@NotNull JWK jwk) throws JOSEException {
        if (jwk.getAlgorithm() == null) {
            log.warn("Unsupported key: " + jwk);
            throw new JOSEException("key algorithm invalid");
        }

        if (jwk instanceof RSAKey && jwk.isPrivate()) {
            // only add the signer if there's a private key
            return new RSASSASigner((RSAKey) jwk);
        } else if (jwk instanceof ECKey && jwk.isPrivate()) {
            // build EC signers & verifiers
            return new ECDSASigner((ECKey) jwk);
        } else if (jwk instanceof OctetSequenceKey) {
            // build HMAC signers & verifiers
            if (jwk.isPrivate()) { // technically redundant check because all HMAC keys are private
                return new MACSigner((OctetSequenceKey) jwk);
            }
        }

        log.warn("Unknown key type: " + jwk);
        return null;
    }

    private JWSVerifier buildVerifier(@NotNull JWK jwk) throws JOSEException {
        if (jwk.getAlgorithm() == null) {
            log.warn("Unsupported key: " + jwk);
            throw new JOSEException("key algorithm invalid");
        }

        if (jwk instanceof RSAKey) {
            return new RSASSAVerifier((RSAKey) jwk);
        } else if (jwk instanceof ECKey) {
            return new ECDSAVerifier((ECKey) jwk);
        } else if (jwk instanceof OctetSequenceKey) {
            return new MACVerifier((OctetSequenceKey) jwk);
        }

        log.warn("Unknown key type: " + jwk);
        return null;
    }

    private JwtDecoder buildJwtDecoder(@NotNull JWK jwk) throws JOSEException {
        //we support only RSA keys
        if (!(jwk instanceof RSAKey)) {
            log.warn("Unsupported key type: " + jwk);
            throw new IllegalArgumentException("the provided key is not suitable for token authentication");
        }

        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withPublicKey(jwk.toRSAKey().toRSAPublicKey()).build();
        OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(issuer);
        OAuth2TokenValidator<Jwt> audienceValidator = new JwtClaimValidator<List<String>>(
            JwtClaimNames.AUD,
            (aud -> aud != null && aud.contains(audience))
        );

        OAuth2TokenValidator<Jwt> validator = new DelegatingOAuth2TokenValidator<>(withIssuer, audienceValidator);
        jwtDecoder.setJwtValidator(validator);

        return jwtDecoder;
    }

    private JwtAuthenticationConverter buildAuthoritiesConverter() {
        JwtAuthenticationConverter authConverter = new JwtAuthenticationConverter();
        authConverter.setPrincipalClaimName(JwtClaimNames.SUB);
        authConverter.setJwtGrantedAuthoritiesConverter((Jwt source) -> {
            if (source == null) return null;

            Set<GrantedAuthority> authorities = new HashSet<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

            List<String> roles = source.getClaimAsStringList(CLAIM_AUTHORITIES);
            if (roles != null) {
                roles.forEach(r -> {
                    //use as is
                    authorities.add(new SimpleGrantedAuthority(r));
                });
            }

            return authorities;
        });

        return authConverter;
    }
}
