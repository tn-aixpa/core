package it.smartcommunitylabdhub.authorization.config;


import it.smartcommunitylabdhub.authorization.components.JWKSetKeyStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import com.nimbusds.jose.JOSEException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


@Configuration
@Slf4j
public class KeyStoreConfig {

    @Value("${keystore.path}")
    private String keyStorePath;

    @Value("${keystore.kid}")
    private String kid;

    private JWKSetKeyStore keyStore;


    @Bean
    @Primary
    public JWKSetKeyStore getJWKSetKeyStore() throws JOSEException {
        Path path = Paths.get(keyStorePath).toAbsolutePath().normalize();
        Resource resource = new FileSystemResource(path);

        if (keyStore == null) {
            if (Files.exists(path) && Files.isReadable(path)) {
                // Load from resource
                keyStore = load(resource);
                // Check if empty
                if (keyStore.getJwk() == null) {
                    // Discard, we will generate a new one
                    keyStore = null;
                }
            }

            if (keyStore == null) {
                // Generate new in-memory keystore
                keyStore = generate();
                // Save to file
                save(keyStore, resource);
            }
        }
        return keyStore;
    }


    private JWKSetKeyStore load(Resource location) throws IllegalArgumentException {
        return new JWKSetKeyStore(location, kid);
    }

    private JWKSetKeyStore generate() throws JOSEException {
        return new JWKSetKeyStore();
    }

    private void save(JWKSetKeyStore keyStore, Resource location) {
        keyStore.saveJwkSet(location);
    }
}
