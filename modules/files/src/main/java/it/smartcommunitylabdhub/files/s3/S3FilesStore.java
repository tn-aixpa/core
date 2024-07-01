package it.smartcommunitylabdhub.files.s3;

import it.smartcommunitylabdhub.commons.models.base.DownloadInfo;
import it.smartcommunitylabdhub.commons.models.base.FileInfo;
import it.smartcommunitylabdhub.commons.models.base.UploadInfo;
import it.smartcommunitylabdhub.files.service.FilesStore;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CompleteMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CompletedMultipartUpload;
import software.amazon.awssdk.services.s3.model.CompletedPart;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadResponse;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.UploadPartRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.CompleteMultipartUploadPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedCompleteMultipartUploadRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedUploadPartRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.UploadPartPresignRequest;

@Slf4j
public class S3FilesStore implements FilesStore {

    public static final int URL_DURATION = 3600 * 8; //8 hours

    private final String accessKey;
    private final String secretKey;
    private final String endpoint;
    private final String bucket;

    private int urlDuration = URL_DURATION;

    private S3Client client;
    private S3Presigner presigner;

    public S3FilesStore(String accessKey, String secretKey, String endpoint, String bucket) {
        Assert.hasText(accessKey, "accessKey is required");

        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.endpoint = endpoint;
        this.bucket = bucket;

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
    public DownloadInfo downloadAsUrl(@NotNull String path) {
        log.debug("generate download url for {}", path);

        //parse as URI where host == bucket, or host == endpoint
        UriComponents uri = UriComponentsBuilder.fromUriString(path).build();
        if (uri.getPath() == null || uri.getHost() == null) {
            return null;
        }

        String bucketName = uri.getHost();
        String key = uri.getPath();

        if (endpoint != null && endpoint.equals(bucketName)) {
            //use first path el as bucket
            bucketName = uri.getPathSegments().stream().findFirst().orElse(null);
            if (bucketName != null) {
                key = uri.getPath().substring(bucketName.length() + 1);
            }
        }

        if (bucketName == null || key == null) {
            return null;
        }

        if (key.startsWith("/")) {
            key = key.substring(1);
        }

        //generate temporary signed url for download
        if (log.isTraceEnabled()) {
            log.trace("generating presigned url for {}: {}", bucketName, key);
        }

        GetObjectRequest req = GetObjectRequest.builder().bucket(bucketName).key(key).build();
        GetObjectPresignRequest preq = GetObjectPresignRequest
            .builder()
            .signatureDuration(Duration.ofSeconds(urlDuration))
            .getObjectRequest(req)
            .build();
        PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(preq);

        DownloadInfo info = new DownloadInfo();
        info.setPath(path);
        info.setUrl(presignedRequest.url().toExternalForm());
        info.setExpiration(presignedRequest.expiration());
        return info;
    }

    @Override
    public List<FileInfo> readMetadata(@NotNull String path) {
        List<FileInfo> result = new ArrayList<>();
        try {
            String[] split = path.replace("s3://", "").split("/");
            String bucketName = split[0];
            String keyName = path.substring(5 + bucketName.length() + 1);
            HeadObjectResponse headObject = client.headObject(
                HeadObjectRequest.builder().bucket(bucketName).key(keyName).build()
            );
            FileInfo response = new FileInfo();
            response.setPath(path);
            response.setName(split[split.length - 1]);
            response.setContentType(headObject.contentType());
            response.setSize(headObject.contentLength());
            response.setLastModified(headObject.lastModified());

            if (StringUtils.hasText(headObject.checksumSHA256())) {
                response.setHash("sha256:" + headObject.checksumSHA256());
            }

            headObject
                .metadata()
                .entrySet()
                .forEach(entry -> {
                    response.getMetadata().put("Metadata." + entry.getKey(), entry.getValue());
                });
            result.add(response);
            return result;
        } catch (Exception e) {
            log.error("generate metadata for {}:  {}", path, e.getMessage());
        }
        return result;
    }

    @Override
    public UploadInfo uploadAsUrl(
        @NotNull String entityType,
        @NotNull String projectId,
        @NotNull String entityId,
        @NotNull String filename
    ) {
        log.debug("generate upload url for {} -> {}", entityId, filename);

        String key = projectId + "/" + entityType + "/" + entityId;
        key += filename.startsWith("/") ? filename : "/" + filename;

        String path = "s3://" + bucket + "/" + key;

        //generate temporary signed url for upload
        if (log.isTraceEnabled()) {
            log.trace("generating presigned url for {}: {}", bucket, key);
        }

        PutObjectRequest req = PutObjectRequest.builder().bucket(bucket).key(key).build();
        PutObjectPresignRequest preq = PutObjectPresignRequest
            .builder()
            .signatureDuration(Duration.ofSeconds(urlDuration))
            .putObjectRequest(req)
            .build();
        PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(preq);

        UploadInfo info = new UploadInfo();
        info.setPath(path);
        info.setUrl(presignedRequest.url().toExternalForm());
        info.setExpiration(presignedRequest.expiration());
        return info;
    }

    @Override
    public UploadInfo startUpload(
        @NotNull String entityType,
        @NotNull String projectId,
        @NotNull String entityId,
        @NotNull String filename
    ) {
        log.debug("generate start upload url for {} -> {}", entityId, filename);
        String key = projectId + "/" + entityType + "/" + entityId;
        key += filename.startsWith("/") ? filename : "/" + filename;

        String path = "s3://" + bucket + "/" + key;

        CreateMultipartUploadRequest req = CreateMultipartUploadRequest.builder().bucket(bucket).key(key).build();
        CreateMultipartUploadResponse response = client.createMultipartUpload(req);

        UploadInfo info = new UploadInfo();
        info.setPath(path);
        info.setUploadId(response.uploadId());
        return info;
    }

    @Override
    public UploadInfo uploadPart(@NotNull String path, @NotNull String uploadId, @NotNull Integer partNumber) {
        log.debug("generate part upload url for {} -> {} - {}", path, uploadId, partNumber);

        String key = path.replace("s3://" + bucket + "/", "");

        UploadPartRequest req = UploadPartRequest
            .builder()
            .bucket(bucket)
            .key(key)
            .uploadId(uploadId)
            .partNumber(partNumber)
            .build();
        UploadPartPresignRequest preq = UploadPartPresignRequest
            .builder()
            .signatureDuration(Duration.ofSeconds(urlDuration))
            .uploadPartRequest(req)
            .build();
        PresignedUploadPartRequest presignedRequest = presigner.presignUploadPart(preq);

        UploadInfo info = new UploadInfo();
        info.setPath(path);
        info.setUrl(presignedRequest.url().toExternalForm());
        info.setExpiration(presignedRequest.expiration());
        return info;
    }

    @Override
    public UploadInfo completeUpload(
        @NotNull String path,
        @NotNull String uploadId,
        @NotNull List<String> eTagPartList
    ) {
        log.debug("generate complete upload url for {} -> {}", path, uploadId);

        String key = path.replace("s3://" + bucket + "/", "");

        List<CompletedPart> parts = new ArrayList<>();
        for (int i = 0; i < eTagPartList.size(); i++) {
            CompletedPart cp = CompletedPart.builder().eTag(eTagPartList.get(i)).partNumber(i + 1).build();
            parts.add(cp);
        }
        CompletedMultipartUpload mp = CompletedMultipartUpload.builder().parts(parts).build();
        CompleteMultipartUploadRequest req = CompleteMultipartUploadRequest
            .builder()
            .bucket(bucket)
            .key(key)
            .uploadId(uploadId)
            .multipartUpload(mp)
            .build();
        CompleteMultipartUploadPresignRequest preq = CompleteMultipartUploadPresignRequest
            .builder()
            .signatureDuration(Duration.ofSeconds(urlDuration))
            .completeMultipartUploadRequest(req)
            .build();
        PresignedCompleteMultipartUploadRequest presignedRequest = presigner.presignCompleteMultipartUpload(preq);

        UploadInfo info = new UploadInfo();
        info.setPath(path);
        info.setUrl(presignedRequest.url().toExternalForm());
        info.setExpiration(presignedRequest.expiration());
        return info;
    }
}
