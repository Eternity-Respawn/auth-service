package dev.eternity.respawn.authservice.dto.User;

import lombok.Data;

@Data
public class UserLoginRequestDto {
    private String email;
    private String password;
}
