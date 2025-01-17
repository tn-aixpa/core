package it.smartcommunitylabdhub.commons.services;

import java.util.Map;

import it.smartcommunitylabdhub.commons.exceptions.StoreException;
import it.smartcommunitylabdhub.commons.exceptions.SystemException;
import it.smartcommunitylabdhub.commons.models.metrics.Metrics;
import jakarta.validation.constraints.NotNull;

public interface MetricsService {
	
	public Map<String, Number[]> getMetrics(@NotNull String entityName, @NotNull String entityId)
			throws StoreException, SystemException;
	
	public Number[] getMetrics(@NotNull String entityName, @NotNull String entityId, @NotNull String name)
			throws StoreException, SystemException;
	
	public Metrics saveMetrics(@NotNull String entityName, @NotNull String entityId, @NotNull String name,
			Number[] values) throws StoreException, SystemException;
}
