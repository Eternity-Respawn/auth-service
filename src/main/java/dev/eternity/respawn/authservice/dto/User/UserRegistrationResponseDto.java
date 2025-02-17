package dev.eternity.respawn.authservice.dto.User;

import lombok.Data;

@Data
public class UserRegistrationResponseDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
}
