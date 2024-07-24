package it.smartcommunitylabdhub.authorization.components;

import com.google.common.io.CharStreams;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import it.smartcommunitylabdhub.authorization.utils.JWKUtils;
import lombok.Getter;
import org.springframework.core.io.Resource;
import com.google.common.base.Charsets;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.ParseException;
import java.util.UUID;

public class JWKSetKeyStore {

    @Getter
    private final JWKSet jwkSet;
    private final String kid;


    public JWKSetKeyStore(Resource location, String kid) throws IllegalArgumentException {
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

    private static JWKSet loadJwkSet(Resource location) throws IllegalArgumentException {
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


    public void saveJwkSet(Resource location) {
        Assert.notNull(location, "Key Set resource cannot be null");
        try {
            Path path = Paths.get(location.getURI());
            if (!Files.exists(path)) {
                // Create directories if they do not exist
                Files.createDirectories(path.getParent());
            }

            try (Writer writer = Files.newBufferedWriter(
                    path, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING)) {
                writer.write(jwkSet.toJSONObject(false).toString());
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Key Set resource could not be written: " + location, e);
        }
    }


    public JWK getJwk() {
        return jwkSet.getKeyByKeyId(kid);
    }
}