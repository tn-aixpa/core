package it.smartcommunitylabdhub.authorization.config;

import com.nimbusds.jose.JOSEException;
import it.smartcommunitylabdhub.authorization.components.JWKSetKeyStore;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

@Configuration
@Slf4j
public class KeyStoreConfig {

    @Value("${jwks.keystore.path}")
    private String keyStorePath;

    @Value("${jwks.keystore.kid}")
    private String kid;

    @Autowired
    private ResourceLoader resourceLoader;

    @Bean
    public JWKSetKeyStore getJWKSetKeyStore() throws JOSEException {
        Resource resource = resourceLoader.getResource(keyStorePath);

        JWKSetKeyStore keyStore = null;

        if (resource.exists() && resource.isReadable()) {
            // Load from resource
            keyStore = new JWKSetKeyStore(resource, kid);
            // Check if empty
            if (keyStore.getJwk() == null) {
                // Discard, we will generate a new one
                keyStore = null;
            }
        }

        if (keyStore == null) {
            // Generate new in-memory keystore
            keyStore = new JWKSetKeyStore();

            // if resource is a file, write it
            if (resource.isFile()) {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(resource.getFile()))) {
                    writer.write(keyStore.getJwkSet().toJSONObject(false).toString());
                } catch (IOException e) {
                    throw new IllegalArgumentException("Key Set resource could not be written: " + keyStorePath, e);
                }
            }
        }

        return keyStore;
    }
}
