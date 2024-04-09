package it.smartcommunitylabdhub.core.models.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(
    name = "labels",
    indexes = {
        @Index(name = "labels_prj_index", columnList = "project"),
        @Index(name = "labels_prj_lbl_index", columnList = "project, label", unique = true),
    }
)
public class LabelEntity implements Serializable {

    @Id
    @Column(unique = true)
    private String id;

    @Column(nullable = false)
    private String project;

    @Column(nullable = false)
    private String label;
}
