package dev.eternity.respawn.authservice.service.impl;

import dev.eternity.respawn.authservice.dto.User.UserRegistrationRequestDto;
import dev.eternity.respawn.authservice.dto.User.UserRegistrationResponseDto;
import dev.eternity.respawn.authservice.exception.UserExistsException;
import dev.eternity.respawn.authservice.mapper.UserMapper;
import dev.eternity.respawn.authservice.model.User;
import dev.eternity.respawn.authservice.repository.UserRepository;
import dev.eternity.respawn.authservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final String REGISTRATION_EXCEPTION_MESSAGE_TEMPLATE
            = "User with email: %s is already exists";

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserRegistrationResponseDto register(UserRegistrationRequestDto requestDto) {
        String email = requestDto.getEmail();

        if (userRepository.findByEmail(email).isPresent()) {
            throw new UserExistsException(String.format(
                    REGISTRATION_EXCEPTION_MESSAGE_TEMPLATE, email
            ));
        }

        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());
        User user = userMapper.toModel(requestDto);
        user.setPassword(encodedPassword);
        user.setRegistrationType(User.RegistrationType.FORM);

        return userMapper.toDto(userRepository.save(user));
    }
}
