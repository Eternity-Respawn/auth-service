package dev.eternity.respawn.authservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "oauth_tokens")
public class OauthToken {
    @Id
    private String accessToken;
    private String refreshToken;
    private Service service;

    private enum Service {
        GOOGLE,
        LINKEDIN
    }
}
