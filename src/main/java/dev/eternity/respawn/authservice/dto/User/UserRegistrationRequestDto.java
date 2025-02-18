package dev.eternity.respawn.authservice.dto.User;

import dev.eternity.respawn.authservice.model.Role;
import lombok.Data;

@Data
public class UserRegistrationRequestDto {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String repeatPassword;
    private Role.RoleName role;
}
