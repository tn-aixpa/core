package it.smartcommunitylabdhub.core.websocket;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.smartcommunitylabdhub.commons.models.base.BaseDTO;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.core.models.events.EntityAction;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class UserNotification<T extends BaseDTO> implements Serializable {

    @Builder.Default
    private String id = UUID.randomUUID().toString();

    @Builder.Default
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    protected OffsetDateTime timestamp = OffsetDateTime.now();

    @NotNull
    private String user;

    @NotNull
    private EntityName entity;

    private EntityAction action;

    @JsonProperty("record")
    @ToString.Exclude
    private T dto;

    public String getResource() {
        //TODO replace with proper function
        return entity.getValue().toLowerCase() + "s";
    }
}
