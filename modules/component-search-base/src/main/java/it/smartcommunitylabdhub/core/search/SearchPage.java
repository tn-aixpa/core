package it.smartcommunitylabdhub.core.search;

import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;

public interface SearchPage<T> extends Page<T> {
    Map<String, List<String>> getFilters();
}
