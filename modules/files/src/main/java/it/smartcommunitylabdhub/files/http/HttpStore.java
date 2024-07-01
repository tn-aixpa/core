package it.smartcommunitylabdhub.files.http;

import it.smartcommunitylabdhub.commons.models.base.DownloadInfo;
import it.smartcommunitylabdhub.commons.models.base.FileInfo;
import it.smartcommunitylabdhub.commons.models.base.UploadInfo;
import it.smartcommunitylabdhub.files.service.FilesStore;
import jakarta.validation.constraints.NotNull;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

@Slf4j
public class HttpStore implements FilesStore {

    public static final String[] PROTOCOLS = { "http", "https", "ftp" };
    private RestTemplate restTemplate = new RestTemplate();

    @Override
    public DownloadInfo downloadAsUrl(@NotNull String path) {
        log.debug("generate download url for {}", path);

        //path must be a valid url
        try {
            URL url = URI.create(path).toURL();
            if (url == null) {
                return null;
            }

            if (!Arrays.asList(PROTOCOLS).contains(url.getProtocol())) {
                //not supported
                return null;
            }

            //use as-is
            DownloadInfo info = new DownloadInfo();
            info.setPath(path);
            info.setUrl(url.toExternalForm());
            return info;
        } catch (MalformedURLException e) {
            //not a valid url...
            return null;
        }
    }

    @Override
    public List<FileInfo> readMetadata(@NotNull String path) {
        List<FileInfo> result = new ArrayList<>();
        try {
            String[] split = path.split("/");
            HttpHeaders headers = restTemplate.headForHeaders(new URI(path));
            FileInfo response = new FileInfo();
            response.setPath(path);
            response.setName(split[split.length - 1]);
            response.setContentType(headers.getContentType().toString());
            response.setSize(headers.getContentLength());
            response.setLastModified(Instant.ofEpochMilli(headers.getLastModified()));
            result.add(response);
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
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public UploadInfo startUpload(
        @NotNull String entityType,
        @NotNull String projectId,
        @NotNull String entityId,
        @NotNull String filename
    ) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public UploadInfo uploadPart(@NotNull String path, @NotNull String uploadId, @NotNull Integer partNumber) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public UploadInfo completeUpload(
        @NotNull String path,
        @NotNull String uploadId,
        @NotNull List<String> eTagPartList
    ) {
        // TODO Auto-generated method stub
        return null;
    }
}
