package it.smartcommunitylabdhub.core.models.queries.filters.entities;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseEntityFilter {

    private String name;

    private String kind;

    private String project;

    private String createdDate;

    private String state;
}
