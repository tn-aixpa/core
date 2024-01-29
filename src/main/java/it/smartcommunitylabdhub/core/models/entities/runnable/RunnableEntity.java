package it.smartcommunitylabdhub.core.models.entities.runnable;

import it.smartcommunitylabdhub.core.annotations.validators.ValidateField;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners({AuditingEntityListener.class})
@Table(name = "runnable")
public class RunnableEntity {

    @Id
    @ValidateField(
            fieldType = "uuid",
            message = "Invalid UUID4 string"
    )
    private String id;

    @Lob
    @NotNull
    private byte[] data;

    @CreatedDate
    @Column(updatable = false)
    private Date created;

    @LastModifiedDate
    private Date updated;

}
