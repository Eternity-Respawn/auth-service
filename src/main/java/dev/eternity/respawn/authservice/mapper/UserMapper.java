package dev.eternity.respawn.authservice.mapper;

import dev.eternity.respawn.authservice.config.MapperConfig;
import dev.eternity.respawn.authservice.dto.User.UserRegistrationRequestDto;
import dev.eternity.respawn.authservice.dto.User.UserRegistrationResponseDto;
import dev.eternity.respawn.authservice.model.User;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    User toModel(UserRegistrationRequestDto requestDto);

    UserRegistrationResponseDto toDto(User user);
}
