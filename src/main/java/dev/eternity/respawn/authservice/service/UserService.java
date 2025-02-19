package dev.eternity.respawn.authservice.service;

import dev.eternity.respawn.authservice.dto.user.UserRegistrationRequestDto;
import dev.eternity.respawn.authservice.dto.user.UserRegistrationResponseDto;

public interface UserService {
    UserRegistrationResponseDto register(UserRegistrationRequestDto requestDto);
}
