package it.smartcommunitylabdhub.commons.models.base;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@Getter
@Setter
public class FileInfo {
	private String path;
	private String name;
	private String contentType;
	private long length;
	private Instant lastModified;
	private String checksumSHA256;
	private Map<String, Serializable> metadata = new HashMap<>();
	
}
