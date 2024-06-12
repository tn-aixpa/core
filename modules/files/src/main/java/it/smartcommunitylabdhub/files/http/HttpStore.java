package it.smartcommunitylabdhub.files.http;

import it.smartcommunitylabdhub.files.service.FilesStore;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpStore implements FilesStore {

    public static final String[] PROTOCOLS = { "http", "https", "ftp" };

    @Override
    public String downloadAsUrl(@NotNull String path) {
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
            return url.toExternalForm();
        } catch (MalformedURLException e) {
            //not a valid url...
            return null;
        }
    }

	@Override
	public Map<String, Serializable> readMetadata(@NotNull String path) {
		// TODO Auto-generated method stub
		return Collections.emptyMap();
	}
}
