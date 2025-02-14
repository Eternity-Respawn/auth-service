package dev.eternity.respawn.authservice.repository;

import dev.eternity.respawn.authservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
