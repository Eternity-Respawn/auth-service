package dev.eternity.respawn.authservice.dto.Github;

import lombok.Data;

@Data
public class GithubEmailDto {
    private String email;
    private boolean primary;
}
