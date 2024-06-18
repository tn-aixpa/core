package it.smartcommunitylabdhub.files.s3;

import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import it.smartcommunitylabdhub.commons.models.base.FileInfo;
import it.smartcommunitylabdhub.files.service.FilesStore;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

@Slf4j
public class S3FilesStore implements FilesStore {

    public static final int URL_DURATION = 3600 * 8; //8 hours

    private final String accessKey;
    private final String secretKey;
    private final String endpoint;

    private int urlDuration = URL_DURATION;

    private S3Client client;
    private S3Presigner presigner;

    public S3FilesStore(String accessKey, String secretKey, String endpoint) {
        Assert.hasText(accessKey, "accessKey is required");

        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.endpoint = endpoint;

        buildClient();
    }

    private void buildClient() {
        //support only basic auth
        AwsBasicCredentials credentials = AwsBasicCredentials
            .builder()
            .accessKeyId(accessKey)
            .secretAccessKey(secretKey)
            .build();

        if (StringUtils.hasText(endpoint)) {
            this.client =
                S3Client
                    .builder()
                    .credentialsProvider(StaticCredentialsProvider.create(credentials))
                    .endpointOverride(URI.create(endpoint))
                    //also enable path style for endpoint by default
                    .forcePathStyle(true)
                    .build();

            this.presigner =
                S3Presigner
                    .builder()
                    .credentialsProvider(StaticCredentialsProvider.create(credentials))
                    .endpointOverride(URI.create(endpoint))
                    .build();
        } else {
            this.client = S3Client.builder().credentialsProvider(StaticCredentialsProvider.create(credentials)).build();
            this.presigner =
                S3Presigner.builder().credentialsProvider(StaticCredentialsProvider.create(credentials)).build();
        }        
    }

    @Override
    public String downloadAsUrl(@NotNull String path) {
        log.debug("generate download url for {}", path);

        //parse as URI where host == bucket, or host == endpoint
        UriComponents uri = UriComponentsBuilder.fromUriString(path).build();
        if (uri.getPath() == null || uri.getHost() == null) {
            return null;
        }

        String bucket = uri.getHost();
        String key = uri.getPath();

        if (endpoint != null && endpoint.equals(bucket)) {
            //use first path el as bucket
            bucket = uri.getPathSegments().stream().findFirst().orElse(null);
            if (bucket != null) {
                key = uri.getPath().substring(bucket.length() + 1);
            }
        }

        if (bucket == null || key == null) {
            return null;
        }

        if (key.startsWith("/")) {
            key = key.substring(1);
        }

        //generate temporary signed url for download
        if (log.isTraceEnabled()) {
            log.trace("generating presigned url for {}: {}", bucket, key);
        }

        GetObjectRequest req = GetObjectRequest.builder().bucket(bucket).key(key).build();
        GetObjectPresignRequest preq = GetObjectPresignRequest
            .builder()
            .signatureDuration(Duration.ofSeconds(urlDuration))
            .getObjectRequest(req)
            .build();
        PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(preq);

        return presignedRequest.url().toExternalForm();
    }

	@Override
	public Map<String, List<FileInfo>> readMetadata(@NotNull String path) {
		Map<String, List<FileInfo>> result = new HashMap<>();
		try {
			String[] split = path.replace("s3://", "").split("/");
			String bucketName = split[0];
			String keyName = path.substring(5 + bucketName.length() + 1);
			HeadObjectResponse headObject = client.headObject(HeadObjectRequest.builder().bucket(bucketName).key(keyName).build());
			FileInfo response = new FileInfo();
			response.setPath(path);
			response.setName(split[split.length - 1]);
			response.setContentType(headObject.contentType());
			response.setLength(headObject.contentLength());
			response.setLastModified(headObject.lastModified());
			response.setChecksumSHA256(headObject.checksumSHA256());
			headObject.metadata().entrySet().forEach(entry -> {
				response.getMetadata().put("Metadata." + entry.getKey(), entry.getValue());
			});
			List<FileInfo> list = new ArrayList<>();
			list.add(response);
			result.put(path, list);
			return result;
		} catch (Exception e) {
			log.error("generate metadata for {}:  {}", path, e.getMessage());
		}
		return result;
	}
}
