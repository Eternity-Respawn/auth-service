package dev.eternity.respawn.authservice.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserResponseDto {
    private String accessToken;
    private String refreshToken;
}
