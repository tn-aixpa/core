package it.smartcommunitylabdhub.authorization.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class RefreshTokenEntity {

    @Id
    private String id;

    private String refreshToken;
    private String subject;
    private Date issuedTime;
    private Date expirationTime;
}
