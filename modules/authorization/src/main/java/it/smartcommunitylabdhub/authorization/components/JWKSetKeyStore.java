package it.smartcommunitylabdhub.authorization.components;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import it.smartcommunitylabdhub.authorization.utils.JWKUtils;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.Optional;
import java.util.UUID;
import lombok.Getter;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class JWKSetKeyStore {

    @Getter
    private final JWKSet jwkSet;

    private final String kid;

    public JWKSetKeyStore(Resource location, String kid) throws IllegalArgumentException {
        this.jwkSet = loadJwkSet(location);
        if (!StringUtils.hasText(kid)) {
            this.kid = jwkSet.getKeys().getFirst().getKeyID();
            Assert.notNull(this.kid, "Key ID cannot be null");
        } else {
            this.kid = jwkSet.getKeyByKeyId(kid).getKeyID();
            Assert.notNull(this.kid, "Key ID cannot be null");
        }
    }

    public JWKSetKeyStore() throws JOSEException {
        this.jwkSet = createJwkSet();
        this.kid =
            Optional
                .of(this.jwkSet)
                .flatMap(set -> set.getKeys().stream().findFirst().map(JWK::getKeyID))
                .orElseThrow(() -> new IllegalStateException("Key ID cannot be null"));

        Assert.notNull(this.kid, "Key ID cannot be null");
    }

    public static JWKSet loadJwkSet(Resource location) throws IllegalArgumentException {
        Assert.notNull(location, "Key Set resource cannot be null");
        if (location.exists() && location.isReadable()) {
            try {
                String s = CharStreams.toString(new InputStreamReader(location.getInputStream(), Charsets.UTF_8));
                return JWKSet.parse(s);
            } catch (IOException e) {
                throw new IllegalArgumentException("Key Set resource could not be read: " + location);
            } catch (ParseException e) {
                throw new IllegalArgumentException("Key Set resource could not be parsed: " + location);
            }
        } else {
            throw new IllegalArgumentException("Key Set resource could not be read: " + location);
        }
    }

    public static JWKSet createJwkSet() throws JOSEException {
        String kid = UUID.randomUUID().toString();
        JWK jwk = JWKUtils.generateRsaJWK(kid, "sig", "RS256", 2048);
        return new JWKSet(jwk);
    }

    public JWK getJwk() {
        return jwkSet.getKeyByKeyId(kid);
    }
}
