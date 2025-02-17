package dev.eternity.respawn.authservice.service;

import dev.eternity.respawn.authservice.dto.User.UserRegistrationRequestDto;
import dev.eternity.respawn.authservice.dto.User.UserRegistrationResponseDto;

public interface UserService {
    UserRegistrationResponseDto register(UserRegistrationRequestDto requestDto);
}
