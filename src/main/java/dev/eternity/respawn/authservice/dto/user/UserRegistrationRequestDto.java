package dev.eternity.respawn.authservice.dto.user;

import dev.eternity.respawn.authservice.model.Role;
import dev.eternity.respawn.authservice.validation.PasswordMatch;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@PasswordMatch
public class UserRegistrationRequestDto {
    private static final String INCORRECT_EMAIL_FORMAT = "Incorrect email format";
    private static final String FIELD_NOT_EMPTY = "This field can't be empty";
    private static final String FROM_8_TO_24_CHARACTERS
            = "Must contain 8 to 24 characters long";
    private static final String INCORRECT_PASSWORD_FORMAT
            = "Password must contain digits, upper and lower case letters";

    @NotBlank(message = FIELD_NOT_EMPTY)
    private String firstName;

    @NotBlank(message = FIELD_NOT_EMPTY)
    private String lastName;

    @Email(message = INCORRECT_EMAIL_FORMAT)
    private String email;

    @Size(min = 8, max = 24, message = FROM_8_TO_24_CHARACTERS)
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d])[a-zA-Z\\d]{8,24}$",
            message = INCORRECT_PASSWORD_FORMAT
    )
    private String password;

    @Size(min = 8, max = 24, message = FROM_8_TO_24_CHARACTERS)
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d])[a-zA-Z\\d]{8,24}$",
            message = INCORRECT_PASSWORD_FORMAT
    )
    private String repeatPassword;

    @NotBlank(message = FIELD_NOT_EMPTY)
    private Role.RoleName role;
}
