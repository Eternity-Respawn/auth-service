package dev.eternity.respawn.authservice.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserLoginRequestDto {
    private static final String INCORRECT_EMAIL_FORMAT = "Incorrect email format";
    private static final String FIELD_NOT_EMPTY = "This field can't be empty";

    @Email(message = INCORRECT_EMAIL_FORMAT)
    private String email;
    @NotBlank(message = FIELD_NOT_EMPTY)
    private String password;
}
