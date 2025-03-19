package it.smartcommunitylabdhub.search;

import it.smartcommunitylabdhub.core.search.ItemResult;
import it.smartcommunitylabdhub.core.search.SearchGroupResult;
import it.smartcommunitylabdhub.core.search.SearchPage;
import it.smartcommunitylabdhub.core.search.indexers.IndexerException;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface SearchService {
    public SearchPage<SearchGroupResult> groupSearch(String q, List<String> fq, Pageable pageRequest)
        throws IndexerException;

    public SearchPage<ItemResult> itemSearch(String q, List<String> fq, Pageable pageRequest) throws IndexerException;
}
