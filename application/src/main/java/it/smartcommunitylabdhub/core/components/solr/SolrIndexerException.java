package it.smartcommunitylabdhub.core.components.solr;

import it.smartcommunitylabdhub.commons.exceptions.StoreException;

public class SolrIndexerException extends StoreException {

    public SolrIndexerException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public SolrIndexerException(String msg) {
        super(msg);
    }
}
