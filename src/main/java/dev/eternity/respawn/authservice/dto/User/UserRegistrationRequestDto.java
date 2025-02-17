package dev.eternity.respawn.authservice.dto.User;

import lombok.Data;

@Data
public class UserRegistrationRequestDto {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String repeatPassword;
}
