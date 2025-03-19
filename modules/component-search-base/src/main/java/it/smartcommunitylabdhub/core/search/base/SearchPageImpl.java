package it.smartcommunitylabdhub.core.search.base;

import it.smartcommunitylabdhub.core.search.SearchPage;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

public class SearchPageImpl<T> extends PageImpl<T> implements SearchPage<T> {

    private static final long serialVersionUID = 8269910872034647723L;

    private final Map<String, List<String>> filters;

    public SearchPageImpl(List<T> content, Pageable pageable, long total, Map<String, List<String>> filters) {
        super(content, pageable, total);
        this.filters = filters;
    }

    @Override
    public Map<String, List<String>> getFilters() {
        return this.filters;
    }
}
