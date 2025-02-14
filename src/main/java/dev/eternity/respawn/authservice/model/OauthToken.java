package dev.eternity.respawn.authservice.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "oauth_tokens")
public class OauthToken {
    @Id
    private String accessToken;
    private String refreshToken;
    private Service service;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private enum Service {
        GOOGLE,
        LINKEDIN
    }
}
