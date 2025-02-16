package dev.eternity.respawn.authservice.dto;

import lombok.Data;

@Data
public class UserLoginRequestDto {
    private String email;
    private String password;
    private String repeatPassword;
}
