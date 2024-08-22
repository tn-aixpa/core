package it.smartcommunitylabdhub.authorization.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "refresh_tokens")
public class RefreshTokenEntity {

    @Id
    private String id;

    @Lob
    private String token;

    private String subject;
    private Date issuedTime;
    private Date expirationTime;
}
