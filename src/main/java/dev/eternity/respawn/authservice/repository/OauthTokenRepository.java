package dev.eternity.respawn.authservice.repository;

import dev.eternity.respawn.authservice.model.OauthToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OauthTokenRepository extends JpaRepository<OauthToken, String> {
}
