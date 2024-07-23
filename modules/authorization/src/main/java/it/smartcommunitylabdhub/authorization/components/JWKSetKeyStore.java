package it.smartcommunitylabdhub.authorization.components;

import com.google.common.io.CharStreams;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import it.smartcommunitylabdhub.authorization.utils.JWKUtils;
import org.springframework.core.io.Resource;
import com.google.common.base.Charsets;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.Optional;
import java.util.UUID;

public class JWKSetKeyStore {

    private final JWKSet jwkSet;
    private String kid;


    public JWKSetKeyStore(Resource location, String kid) {
        this.jwkSet = loadJwkSet(location);
        if (!StringUtils.hasText(kid)) {
            this.kid = jwkSet.getKeys().getFirst().getKeyID(); // prendo la prima
            // se è null, non ci sono chiavi assert
            Assert.notNull(this.kid, "Key ID cannot be null");
        } else {
            this.kid = jwkSet.getKeyByKeyId(kid).getKeyID();
            Assert.notNull(this.kid, "Key ID cannot be null");
        }
    }

    public JWKSetKeyStore() throws JOSEException {
        this.jwkSet = initializeJwkSet();
        this.kid = this.jwkSet.getKeys().getFirst().getKeyID();
        Assert.notNull(this.kid, "Key ID cannot be null");
    }

    private static JWKSet loadJwkSet(Resource location) {
        Assert.notNull(location, "Key Set resource cannot be null");
        if (location.exists() && location.isReadable()) {
            try {
                String s = CharStreams.toString(
                        new InputStreamReader(location.getInputStream(), Charsets.UTF_8)
                );
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

    private static JWKSet initializeJwkSet() throws JOSEException {
        String kid = UUID.randomUUID().toString();
        JWK jwk = JWKUtils.generateRsaJWK(kid, "sig", "RS256", 2048);
        return new JWKSet(jwk);
    }


    public JWKSet getJwkSet() {
        return jwkSet;
    }

    public JWK getJwk() {
        return jwkSet.getKeyByKeyId(kid);
    }
}