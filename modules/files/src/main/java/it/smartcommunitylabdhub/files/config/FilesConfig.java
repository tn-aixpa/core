package it.smartcommunitylabdhub.files.config;

import it.smartcommunitylabdhub.files.http.HttpStore;
import it.smartcommunitylabdhub.files.s3.S3FilesStore;
import it.smartcommunitylabdhub.files.service.FilesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilesConfig {

    @Autowired
    private FilesService service;

    // @ConditionalOnProperty(name = "files.store.s3.access-key", matchIfMissing = false)
    @ConditionalOnExpression("!T(org.springframework.util.StringUtils).isEmpty('${files.store.s3.access-key:}')")
    @Bean
    public S3FilesStore s3FilesStore(
        @Value("${files.store.s3.access-key}") String accessKey,
        @Value("${files.store.s3.secret-key}") String secretKey,
        @Value("${files.store.s3.endpoint}") String endpoint,
        @Value("${files.store.s3.bucket}") String bucket
    ) {
        S3FilesStore store = new S3FilesStore(accessKey, secretKey, endpoint, bucket);

        //register as default
        service.registerStore("s3://", store);

        return store;
    }

    @Bean
    public HttpStore httpFilesStore() {
        HttpStore store = new HttpStore();

        //register as default
        service.registerStore("http://", store);
        service.registerStore("https://", store);
        service.registerStore("ftp://", store);

        return store;
    }
}
