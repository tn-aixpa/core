package it.smartcommunitylabdhub.commons.services;

import java.util.Map;

import it.smartcommunitylabdhub.commons.exceptions.StoreException;
import it.smartcommunitylabdhub.commons.exceptions.SystemException;
import it.smartcommunitylabdhub.commons.models.base.BaseDTO;
import it.smartcommunitylabdhub.commons.models.metrics.Metrics;
import it.smartcommunitylabdhub.commons.models.metrics.NumberOrNumberArray;
import jakarta.validation.constraints.NotNull;

public interface MetricsService<T extends BaseDTO> {
	
	public Map<String, NumberOrNumberArray> getMetrics(@NotNull String entityId)
			throws StoreException, SystemException;
	
	public NumberOrNumberArray getMetrics(@NotNull String entityId, @NotNull String name)
			throws StoreException, SystemException;
	
	public Metrics saveMetrics(@NotNull String entityId, @NotNull String name,
			NumberOrNumberArray data) throws StoreException, SystemException;
}
