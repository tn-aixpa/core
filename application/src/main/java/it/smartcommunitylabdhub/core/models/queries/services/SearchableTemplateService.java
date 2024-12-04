package it.smartcommunitylabdhub.core.models.queries.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import it.smartcommunitylabdhub.commons.exceptions.SystemException;
import it.smartcommunitylabdhub.commons.models.template.Template;
import it.smartcommunitylabdhub.core.models.queries.filters.abstracts.TemplateFilter;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/*
 * Searchable service for managing function
 */
public interface SearchableTemplateService {
    /**
     * List all functions, with optional filters
     * @param pageable
     * @param filter
     * @return
     */
    Page<Template> searchTemplates(Pageable pageable,  @NotNull String type, @Valid TemplateFilter filter)
        throws SystemException;
    
    Template getTemplate(@NotNull String type, @NotNull String id)
            throws SystemException;

}
