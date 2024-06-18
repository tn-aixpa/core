package it.smartcommunitylabdhub.commons.models.base;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@Getter
@Setter
public class DownloadInfo {
	private String path;
	private String url;
	private Instant expiration;
	
	@Override
	public String toString() {
		return path + "_" + url + "_" + (expiration != null ? expiration.toEpochMilli() : "0");
	}
}
