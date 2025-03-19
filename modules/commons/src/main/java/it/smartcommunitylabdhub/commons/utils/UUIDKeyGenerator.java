package it.smartcommunitylabdhub.commons.utils;

import java.util.UUID;
import org.springframework.security.crypto.keygen.StringKeyGenerator;

public class UUIDKeyGenerator implements StringKeyGenerator {

    @Override
    public String generateKey() {
        //return a unique string by leveraging UUID
        return UUID.randomUUID().toString().replace("-", "");
    }
}
