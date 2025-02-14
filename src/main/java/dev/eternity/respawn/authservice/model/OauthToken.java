package dev.eternity.respawn.authservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Data
@Entity
@Table(name = "oauth_tokens")
@NoArgsConstructor
public class OauthToken {
    @Id
    @Column(name = "access_token", nullable = false)
    private String accessToken;
    @Column(name = "refresh_token", nullable = false)
    private String refreshToken;
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false)
    private Service service;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private enum Service {
        GOOGLE,
        LINKEDIN
    }
}
