package dev.eternity.respawn.authservice.dto.user;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserResponseDto {
    private String accessToken;
}
